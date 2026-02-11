package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import lombok.Setter;

/**
 * Represents a variable that can assigned in Wenyan
 */
@Setter
public class WenyanLeftValue implements IWenyanValue {
    /** The actual value stored in this variable */
    public IWenyanValue value;

    /**
     * Creates a new left value
     * @param value The initial value
     */
    public WenyanLeftValue(IWenyanValue value) {
        this.value = value;
    }

    /**
     * Creates a variable from a value if not already a variable
     * @param value The value to wrap
     * @return A variable containing the value
     */
    public static IWenyanValue varOf(IWenyanValue value) {
        if (value instanceof WenyanLeftValue leftValue) {
            return leftValue;
        } else {
            return new WenyanLeftValue(value);
        }
    }

    @Override
    public WenyanType<?> type() {
        return value.type();
    }

    @Override
    public <T extends IWenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        // turn into right if casting
        return value.as(type);
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
