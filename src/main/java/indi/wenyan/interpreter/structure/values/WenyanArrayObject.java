package indi.wenyan.interpreter.structure.values;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class WenyanArrayObject implements WenyanObject {
    public static final WenyanType<WenyanArrayObject> TYPE = new WenyanType<>("list");
    public final List<WenyanValue> values;

    public WenyanArrayObject() {
        values = new ArrayList<>();
    }
    public WenyanArrayObject(List<WenyanValue> values) {
        this.values = values;
    }

    public WenyanArrayObject concat(WenyanArrayObject other) {
        values.addAll(other.values);
        return this;
    }

    public void add(WenyanValue wenyanValue) {
        values.add(wenyanValue);
    }

    public WenyanValue get(int index) throws WenyanException.WenyanThrowException {
        try {
            return values.get(index-1);
        } catch (IndexOutOfBoundsException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public void setVariable(String name, WenyanValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public WenyanValue getAttribute(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID ->
                    new LocalCallHandler((self, args) -> {
                        if (args.size() != 1)
                            throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                        return self.as(TYPE).get(args.getFirst().as(WenyanInteger.TYPE).value);
                    });
            case WenyanDataParser.ITER_ID ->
                    new LocalCallHandler((self, args) -> {
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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type == TYPE) {
            return (T) this;
        }
        if (type == WenyanObject.TYPE) {
            return (T) this;
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        throw new WenyanException("");
    }
}
