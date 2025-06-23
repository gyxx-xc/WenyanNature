package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanComparable;
import indi.wenyan.interpreter.structure.WenyanComputable;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public class WenyanDouble implements WenyanComputable, WenyanComparable {
    public static final WenyanType<WenyanDouble> TYPE = new WenyanType<>("double");
    public Double value;
    public boolean isConstant;

    public WenyanDouble(double value) {
        this.value = value;
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
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type == WenyanInteger.TYPE) {
            return (T) new WenyanInteger(value.intValue());
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    @Override
    public int compareTo(WenyanValue value) throws WenyanException.WenyanTypeException {
        return this.value.compareTo(value.as(TYPE).value);
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        this.value = value.as(TYPE).value;
    }

    @Override
    public String toString() {
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        String dot = "又";
        StringBuilder result = new StringBuilder();
        if (value < 0) {
            result.append("負");
            value = -value;
        }
        for (char digit : Double.toString(value).toCharArray())
            result.append(digit == '.' ? dot : numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }
}
