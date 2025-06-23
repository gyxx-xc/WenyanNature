package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

public record WenyanString(String value) implements WenyanValue {
    public static final WenyanType<WenyanString> TYPE = new WenyanType<>("string", WenyanString.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return value;
    }
}
