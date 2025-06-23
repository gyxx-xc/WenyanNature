package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanComparable;
import indi.wenyan.interpreter.structure.WenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

public record WenyanDouble(Double value) implements WenyanComputable, WenyanComparable {
    public static final WenyanType<WenyanDouble> TYPE = new WenyanType<>("double", WenyanDouble.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanInteger.TYPE) {
            return (T) new WenyanInteger(value.intValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        return null;
    }

    @Override
    public WenyanDouble add(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value + other.as(TYPE).value);
    }

    @Override
    public WenyanDouble subtract(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value - other.as(TYPE).value);
    }

    @Override
    public WenyanDouble multiply(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value * other.as(TYPE).value);
    }

    @Override
    public WenyanDouble divide(WenyanValue other) throws WenyanException.WenyanTypeException {
        return new WenyanDouble(value / other.as(TYPE).value);
    }

    @Override
    public int compareTo(WenyanValue value) throws WenyanException.WenyanTypeException {
        return this.value.compareTo(value.as(TYPE).value);
    }

    @Override
    public String toString() {
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        String dot = "又";
        StringBuilder result = new StringBuilder();
        double v = value;
        if (v < 0) {
            result.append("負");
            v = -v;
        }
        for (char digit : Double.toString(v).toCharArray())
            result.append(digit == '.' ? dot : numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }
}
