package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.JavacallHandler;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;

public class WenyanArrayObject extends WenyanObject {
    private final ArrayList<WenyanValue> values = new ArrayList<>();
    private final WenyanValue length = new WenyanValue(WenyanValue.Type.INT, 0, true);

    public WenyanArrayObject() {
        super(new WenyanArrayObjectType());
        variable.put("長", length);
    }

    public WenyanArrayObject concat(WenyanArrayObject other) {
        values.addAll(other.values);
        length.setValue(values.size());
        return this;
    }

    public void add(WenyanValue wenyanValue) {
        values.add(wenyanValue);
        length.setValue(values.size());
    }

    public WenyanValue get(WenyanValue index) throws WenyanException.WenyanThrowException {
        try {
            return values.get((int) index.casting(WenyanValue.Type.INT).getValue() - 1);
        } catch (RuntimeException e) {
            throw new WenyanException.WenyanDataException(e.getMessage());
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (WenyanValue wenyanValue : values)
            result.append(result.isEmpty() ? "" : " ").append(wenyanValue.toString());
        return result.toString();
    }

    public static class WenyanArrayObjectType extends WenyanObjectType {
        public WenyanArrayObjectType() {
            super(null, "列");
            staticVariable.put("GET", new WenyanValue(WenyanValue.Type.FUNCTION,
                    new WenyanValue.FunctionSign("GET", // TODO: change names
                            new WenyanValue.Type[]{WenyanValue.Type.LIST, WenyanValue.Type.INT},
                            new JavacallHandler(args -> {
                                if (args.length != 2)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanValue.Type.LIST);
                                args[1].casting(WenyanValue.Type.INT);
                                return ((WenyanArrayObject) args[0].getValue()).get(args[1]);
                            })), true));
        }
    }
}
