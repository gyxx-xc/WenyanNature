package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanComparable;
import indi.wenyan.interpreter.structure.values.IWenyanComputable;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an integer value in Wenyan language.
 * Supports arithmetic operations and comparisons.
 */
public record WenyanInteger(Integer value)
        implements IWenyanWarperValue<Integer>, IWenyanComputable, IWenyanComparable {
    public static final WenyanType<WenyanInteger> TYPE = new WenyanType<>("int", WenyanInteger.class);

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value + other.as(TYPE).value);
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value - other.as(TYPE).value);
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value * other.as(TYPE).value);
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble((double)value / other.as(TYPE).value);
    }

    public WenyanInteger mod(WenyanInteger other) {
        return new WenyanInteger(value % other.value);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanDouble.TYPE) {
            return (T) new WenyanDouble(value.doubleValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        if (type == WenyanBoolean.TYPE) {
            return (T) new WenyanBoolean(value != 0);
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull IWenyanValue value) throws WenyanException.WenyanTypeException {
        return this.value.compareTo(value.as(TYPE).value);
    }

    @Override
    public @NotNull String toString() {
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        StringBuilder result = new StringBuilder();
        int v = value;
        if (v < 0) {
            result.append("負");
            v = -v;
        }
        for (char digit : Integer.toString(v).toCharArray())
            result.append(numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }
}
