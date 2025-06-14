package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class WenyanArrayObject implements WenyanObject {
    private final ArrayList<WenyanNativeValue> values = new ArrayList<>();

    public WenyanArrayObject() {}

    public WenyanArrayObject concat(WenyanArrayObject other) {
        values.addAll(other.values);
        return this;
    }

    public void add(WenyanNativeValue wenyanValue) {
        values.add(wenyanValue);
    }

    public WenyanNativeValue get(WenyanNativeValue index) throws WenyanException.WenyanThrowException {
        try {
            return values.get((int) index.casting(WenyanType.INT).getValue() - 1);
        } catch (RuntimeException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
    }

    @Override
    public WenyanNativeValue getAttribute(String name) {
        return switch (name) {
            case WenyanDataParser.ARRAY_GET_ID -> new WenyanNativeValue(WenyanType.FUNCTION,
                    new WenyanNativeValue.FunctionSign(WenyanDataParser.ARRAY_GET_ID,
                            new WenyanType[]{WenyanType.LIST, WenyanType.INT},
                            new LocalCallHandler(args -> {
                                if (args.length != 2)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanType.LIST);
                                args[1].casting(WenyanType.INT);
                                return ((WenyanArrayObject) args[0].getValue()).get(args[1]);
                            })), true);
            case WenyanDataParser.ITER_ID -> new WenyanNativeValue(WenyanType.FUNCTION,
                    new WenyanNativeValue.FunctionSign(WenyanDataParser.ITER_ID,
                            new WenyanType[]{WenyanType.LIST},
                            new LocalCallHandler(args -> {
                                if (args.length != 1)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanType.LIST);
                                return new WenyanNativeValue(WenyanType.OBJECT,
                                        ((WenyanArrayObject) args[0].getValue()).values.iterator(), true);
                            })), true);
            case WenyanDataParser.LONG_ID -> new WenyanNativeValue(WenyanType.INT, values.size(), true);
            default -> throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + name);
        };
    }

    @Override
    public WenyanObjectType getParent() {
        return null;
    }

    @Override
    public WenyanType type() {
        return WenyanType.LIST;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
