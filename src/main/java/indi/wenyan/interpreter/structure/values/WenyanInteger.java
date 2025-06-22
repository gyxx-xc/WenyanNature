package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class WenyanInteger implements WenyanComputable, WenyanComparable {
    public static final WenyanType<WenyanInteger> TYPE = new WenyanType<>("int");
    public Integer value;
    public boolean isConstant;

    public WenyanInteger(int value) {
        this.value = value;
    }

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

    public WenyanInteger mod(WenyanInteger other) throws WenyanException.WenyanTypeException {
        return new WenyanInteger(value % other.value);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (this.type() == type)
            return (T) this;
        if (type == WenyanDouble.TYPE) {
            return (T) new WenyanDouble(value);
        }
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    @Override
    public int compareTo(@NotNull WenyanValue value) throws WenyanException.WenyanTypeException {
        return value.as(TYPE).value - this.value;
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        this.value = value.as(TYPE).value;
    }

    @Override
    public String toString() {
        String[] numerals = {"零", "壹", "貳", "參", "肆", "伍", "陸", "柒", "捌", "玖"};
        StringBuilder result = new StringBuilder();
        if (value < 0) {
            result.append("負");
            value = -value;
        }
        for (char digit : Integer.toString(value).toCharArray())
            result.append(numerals[Character.getNumericValue(digit)]);
        return result.toString();
    }
}
