package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.handler.LocalCallHandler;
import indi.wenyan.interpreter.utils.JavaCallCodeWarper;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class WenyanArrayObject extends WenyanObject {
    private final ArrayList<WenyanValue> values = new ArrayList<>();
    private final WenyanValue length = new WenyanValue(WenyanValue.Type.INT, 0, true);

    public WenyanArrayObject() {
        super(new WenyanArrayObjectType());
        variable.put(WenyanDataParser.LONG_ID, length);
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
        return values.toString();
    }

    public static class WenyanArrayObjectType extends WenyanObjectType {
        public WenyanArrayObjectType() {
            super(null, "列");
            functions.put(WenyanDataParser.ARRAY_GET_ID, new WenyanValue(WenyanValue.Type.FUNCTION,
                    new WenyanValue.FunctionSign(WenyanDataParser.ARRAY_GET_ID,
                            new WenyanValue.Type[]{WenyanValue.Type.LIST, WenyanValue.Type.INT},
                            new JavaCallCodeWarper(new LocalCallHandler(args -> {
                                if (args.length != 2)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanValue.Type.LIST);
                                args[1].casting(WenyanValue.Type.INT);
                                return ((WenyanArrayObject) args[0].getValue()).get(args[1]);
                            }))), true));
            functions.put(WenyanDataParser.ITER_ID, new WenyanValue(WenyanValue.Type.FUNCTION,
                    new WenyanValue.FunctionSign(WenyanDataParser.ITER_ID,
                            new WenyanValue.Type[]{WenyanValue.Type.LIST},
                            new JavaCallCodeWarper(new LocalCallHandler(args -> {
                                if (args.length != 1)
                                    throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                                args[0].casting(WenyanValue.Type.LIST);
                                return new WenyanValue(WenyanValue.Type.OBJECT,
                                        ((WenyanArrayObject) args[0].getValue()).values.iterator(), true);
                            }))), true));
        }
    }
}
