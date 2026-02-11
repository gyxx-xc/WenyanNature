package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.WenyanType;

public interface IWenyanComparable extends IWenyanValue {
    WenyanType<IWenyanComparable> TYPE = new WenyanType<>("comparable", IWenyanComparable.class);

    /**
     * Compare this value with another value.
     *
     * @param other the other value to compare with
     * @return a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the specified value
     * @throws WenyanException.WenyanTypeException if the types are incompatible for comparison
     */
    int compareTo(IWenyanValue other) throws WenyanThrowException;
}
