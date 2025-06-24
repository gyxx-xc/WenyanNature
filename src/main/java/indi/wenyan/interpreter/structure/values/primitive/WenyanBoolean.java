package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

public record WenyanBoolean(Boolean value) implements IWenyanValue {
    public static final WenyanType<WenyanBoolean> TYPE = new WenyanType<>("bool", WenyanBoolean.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        return null;
    }

    public WenyanBoolean not() {
        return new WenyanBoolean(!value);
    }

    @Override
    public String toString() {
        return value ? "陽" : "陰";
    }
}
