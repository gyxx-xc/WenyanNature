package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.Iterator;

public record WenyanIterator(Iterator<IWenyanValue> value) implements IWenyanValue {
    public static final WenyanType<WenyanIterator> TYPE = new WenyanType<>("iterator", WenyanIterator.class);
    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
