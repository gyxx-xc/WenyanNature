package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.List;

public record WenyanArrayList(List<IWenyanValue> values) implements IWenyanObject {
    public static final WenyanType<WenyanArrayList> TYPE = new WenyanType<>("list", WenyanArrayList.class);

    public WenyanArrayList concat(WenyanArrayList other) {
        values.addAll(other.values);
        return this;
    }

    public void add(IWenyanValue wenyanValue) {
        values.add(wenyanValue);
    }

    public IWenyanValue get(int index) throws WenyanException.WenyanThrowException {
        try {
            return values.get(index - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new LocalCallHandler((self, args) -> {
                if (args.size() != 1)
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                return self.as(TYPE).get(args.getFirst().as(WenyanInteger.TYPE).value());
            });
            case WenyanDataParser.ITER_ID -> new LocalCallHandler((self, args) -> {
                if (!args.isEmpty())
                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                return new WenyanIterator(self.as(TYPE).values.iterator());
            });
            case WenyanDataParser.LONG_ID -> new WenyanInteger(values.size());
            default -> throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
        };
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
