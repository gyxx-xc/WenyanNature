package indi.wenyan.judou.structure.values.primitive;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a boolean value in Wenyan language.
 * Values are represented as "陽" (true) and "陰" (false).
 */
public enum WenyanBoolean implements IWenyanValue {
    TRUE(true),
    FALSE(false);

    private final boolean value;
    WenyanBoolean(boolean value) {
        this.value = value;
    }

    public static final WenyanType<WenyanBoolean> TYPE = new WenyanType<>("bool", WenyanBoolean.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IWenyanValue> @Nullable T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE)
            return (T) WenyanValues.of(toString());
        if (type == WenyanInteger.TYPE)
            return (T) WenyanValues.of(value ? 1 : 0);
        return null;
    }

    public WenyanBoolean not() {
        return value ? FALSE : TRUE;
    }

    @Override
    public @NotNull String toString() {
        return value ? "陽" : "陰";
    }

    public boolean value() throws WenyanException {
        return value;
    }
}
