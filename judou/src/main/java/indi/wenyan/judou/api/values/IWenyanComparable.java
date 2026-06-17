package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.exception.WenyanException;

public interface IWenyanComparable extends IWenyanValue {
    WenyanType<IWenyanComparable> TYPE = new WenyanType<>(JudouTypeText.Comparable.string(), IWenyanComparable.class);

    /// Compare this value with another value.
    ///
    /// @param other the other value to compare with
    /// @return a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the specified value
    /// @throws WenyanException.WenyanTypeException if the types are incompatible for comparison
    int compareTo(IWenyanValue other) throws WenyanException;
}
