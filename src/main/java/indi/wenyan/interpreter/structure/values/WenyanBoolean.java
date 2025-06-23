package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

public record WenyanBoolean(Boolean value) implements WenyanValue {
    public static final WenyanType<WenyanBoolean> TYPE = new WenyanType<>("bool", WenyanBoolean.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        return null;
    }

    public WenyanBoolean not() throws WenyanException.WenyanTypeException {
        return new WenyanBoolean(!value);
    }

    @Override
    public String toString() {
        return value ? "陽" : "陰";
    }
}
