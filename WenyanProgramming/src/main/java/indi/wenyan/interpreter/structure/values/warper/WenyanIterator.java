package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;

import java.util.Iterator;

/**
 * Wrapper for an iterator of Wenyan values.
 * Used for iteration operations in the language.
 */
public record WenyanIterator(Iterator<IWenyanValue> value)
        implements IWenyanWarperValue<Iterator<IWenyanValue>> {
    public static final WenyanType<WenyanIterator> TYPE = new WenyanType<>("iterator", WenyanIterator.class);
    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
