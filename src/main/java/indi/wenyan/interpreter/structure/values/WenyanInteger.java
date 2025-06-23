package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanComparable;
import indi.wenyan.interpreter.structure.WenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WenyanInteger(Integer value) implements WenyanComputable, WenyanComparable {
    public static final WenyanType<WenyanInteger> TYPE = new WenyanType<>("int", WenyanInteger.class);

    @Override
    public WenyanValue add(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value + other.as(TYPE).value);
    }

    @Override
    public WenyanValue subtract(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value - other.as(TYPE).value);
    }

    @Override
    public WenyanValue multiply(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value * other.as(TYPE).value);
    }

    @Override
    public WenyanValue divide(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value / other.as(TYPE).value);
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
    public @Nullable <T extends WenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanDouble.TYPE) {
            return (T) new WenyanDouble(value.doubleValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        return null;
    }

    @Override
    public int compareTo(@NotNull WenyanValue value) throws WenyanException.WenyanTypeException {
        return value.as(TYPE).value - this.value;
    }

    @Override
    public String toString() {
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
