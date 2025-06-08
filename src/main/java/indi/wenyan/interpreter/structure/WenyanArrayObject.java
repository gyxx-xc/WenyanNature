package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class WenyanArrayObject implements WenyanObject {
    private final ArrayList<WenyanValue> values = new ArrayList<>();

    public WenyanArrayObject() {
    }

    public WenyanArrayObject concat(WenyanArrayObject other) {
        values.addAll(other.values);
        return this;
    }

    public void add(WenyanValue wenyanValue) {
        values.add(wenyanValue);
    }

    public WenyanValue get(WenyanValue index) throws WenyanException.WenyanThrowException {
        try {
            return values.get((int) index.casting(WenyanValue.Type.INT).getValue() - 1);
        } catch (RuntimeException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public WenyanValue getVariable(String name) {
        if (name.equals(WenyanDataParser.LONG_ID)) {
            return new WenyanValue(WenyanValue.Type.INT, values.size(), true);
        }
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public void setVariable(String name, WenyanValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public WenyanValue getFunction(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanValue(WenyanValue.Type.FUNCTION,
                    new WenyanValue.FunctionSign(WenyanDataParser.ARRAY_GET_ID,
                            new WenyanValue.Type[]{WenyanValue.Type.LIST, WenyanValue.Type.INT},
                            new LocalCallHandler(args -> {
                                if (args.length != 2)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanValue.Type.LIST);
                                args[1].casting(WenyanValue.Type.INT);
                                return ((WenyanArrayObject) args[0].getValue()).get(args[1]);
                            })), true);
            case WenyanDataParser.ITER_ID -> new WenyanValue(WenyanValue.Type.FUNCTION,
                    new WenyanValue.FunctionSign(WenyanDataParser.ITER_ID,
                            new WenyanValue.Type[]{WenyanValue.Type.LIST},
                            new LocalCallHandler(args -> {
                                if (args.length != 1)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanValue.Type.LIST);
                                return new WenyanValue(WenyanValue.Type.OBJECT,
                                        ((WenyanArrayObject) args[0].getValue()).values.iterator(), true);
                            })), true);
            default -> throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_").getString() + name);
        };
    }

    @Override
    public WenyanObjectType getType() {
        return null;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
