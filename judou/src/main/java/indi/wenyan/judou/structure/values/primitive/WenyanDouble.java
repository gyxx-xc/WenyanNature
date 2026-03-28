package indi.wenyan.judou.structure.values.primitive;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanComparable;
import indi.wenyan.judou.structure.values.IWenyanComputable;
import indi.wenyan.judou.structure.values.IWenyanNumber;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.function.ChineseUtils;
import indi.wenyan.judou.utils.function.WenyanValues;
import indi.wenyan.judou.utils.language.JudouTypeText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a double-precision floating point value in Wenyan language.
 * Supports arithmetic operations and comparisons.
 */
public record WenyanDouble(double value)
        implements IWenyanComputable, IWenyanComparable, IWenyanNumber {
    public static final WenyanType<WenyanDouble> TYPE = new WenyanType<>(JudouTypeText.Double.string(), WenyanDouble.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IWenyanValue> @Nullable T casting(WenyanType<T> type) {
        if (type == WenyanInteger.TYPE) {
            return (T) WenyanValues.of((int) value);
        }
        if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(toString());
        }
        if (type == WenyanBoolean.TYPE) {
            return (T) WenyanValues.of(value != 0);
        }
        return null;
    }

    @Override
    public WenyanDouble add(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value + other.as(TYPE).value);
    }

    @Override
    public WenyanDouble subtract(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value - other.as(TYPE).value);
    }

    @Override
    public WenyanDouble multiply(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value * other.as(TYPE).value);
    }

    @Override
    public WenyanDouble divide(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value / other.as(TYPE).value);
    }

    @Override
    public int compareTo(IWenyanValue value) throws WenyanException.WenyanTypeException {
        return Double.compare(this.value, value.as(TYPE).value);
    }

    @Override
    public @NotNull String toString() {
        // replace "點" with "又"
        return ChineseUtils.toChinese(value);
    }
}
