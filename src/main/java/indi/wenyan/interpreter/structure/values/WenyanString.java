package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

public class WenyanString implements WenyanValue{
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>("string");
    public String value;

    public WenyanString(String value) {
        this.value = value;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        return null;
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        this.value = value.as(TYPE).value;
    }

    @Override
    public String toString() {
        return value;
    }
}
