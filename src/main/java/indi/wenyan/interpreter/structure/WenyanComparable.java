package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.structure.values.WenyanValue;

public interface WenyanComparable extends WenyanValue {
    /**
     * Compare this value with another value.
     *
     * @param other the other value to compare with
     * @return a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the specified value
     * @throws WenyanException.WenyanTypeException if the types are incompatible for comparison
     */
    int compareTo(WenyanValue other) throws WenyanException.WenyanThrowException;
}
