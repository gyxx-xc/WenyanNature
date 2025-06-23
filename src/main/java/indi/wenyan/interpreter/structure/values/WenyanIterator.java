package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

import java.util.Iterator;

public record WenyanIterator(Iterator<WenyanValue> value) implements WenyanValue {
    public static final WenyanType<WenyanIterator> TYPE = new WenyanType<>("iterator", WenyanIterator.class);
    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
