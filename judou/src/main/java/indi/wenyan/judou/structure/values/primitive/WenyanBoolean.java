package indi.wenyan.judou.structure.values.primitive;

import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.IWenyanWarperValue;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a boolean value in Wenyan language.
 * Values are represented as "陽" (true) and "陰" (false).
 */
public record WenyanBoolean(Boolean value) implements IWenyanWarperValue<Boolean> {
    public static final WenyanType<WenyanBoolean> TYPE = new WenyanType<>("bool", WenyanBoolean.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE)
            return (T) WenyanValues.of(toString());
        if (type == WenyanInteger.TYPE)
            return (T) WenyanValues.of(value ? 1 : 0);
        return null;
    }

    public WenyanBoolean not() {
        return new WenyanBoolean(!value);
    }

    @Override
    public @NotNull String toString() {
        return value ? "陽" : "陰";
    }
}
