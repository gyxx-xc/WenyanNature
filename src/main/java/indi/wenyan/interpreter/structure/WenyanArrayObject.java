package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class WenyanArrayObject implements WenyanObject {
    private final ArrayList<WenyanNativeValue> values = new ArrayList<>();

    public WenyanArrayObject() {
    }

    public WenyanArrayObject concat(WenyanArrayObject other) {
        values.addAll(other.values);
        return this;
    }

    public void add(WenyanNativeValue wenyanValue) {
        values.add(wenyanValue);
    }

    public WenyanNativeValue get(WenyanNativeValue index) throws WenyanException.WenyanThrowException {
        try {
            return values.get((int) index.casting(WenyanNativeValue.Type.INT).getValue() - 1);
        } catch (RuntimeException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public WenyanNativeValue getVariable(String name) {
        if (name.equals(WenyanDataParser.LONG_ID)) {
            return new WenyanNativeValue(WenyanNativeValue.Type.INT, values.size(), true);
        }
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public WenyanNativeValue getFunction(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanNativeValue(WenyanNativeValue.Type.FUNCTION,
                    new WenyanNativeValue.FunctionSign(WenyanDataParser.ARRAY_GET_ID,
                            new WenyanNativeValue.Type[]{WenyanNativeValue.Type.LIST, WenyanNativeValue.Type.INT},
                            new LocalCallHandler(args -> {
                                if (args.length != 2)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanNativeValue.Type.LIST);
                                args[1].casting(WenyanNativeValue.Type.INT);
                                return ((WenyanArrayObject) args[0].getValue()).get(args[1]);
                            })), true);
            case WenyanDataParser.ITER_ID -> new WenyanNativeValue(WenyanNativeValue.Type.FUNCTION,
                    new WenyanNativeValue.FunctionSign(WenyanDataParser.ITER_ID,
                            new WenyanNativeValue.Type[]{WenyanNativeValue.Type.LIST},
                            new LocalCallHandler(args -> {
                                if (args.length != 1)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanNativeValue.Type.LIST);
                                return new WenyanNativeValue(WenyanNativeValue.Type.OBJECT,
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
