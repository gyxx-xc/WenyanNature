package indi.wenyan.judou.structure.values;

import indi.wenyan.judou.structure.WenyanThrowException;

/**
 * Interface for Wenyan values that support basic arithmetic operations.
 */
public interface IWenyanComputable extends IWenyanValue {
    /**
     * Adds this value to another value.
     *
     * @param other the value to add to this value
     * @return the result of the addition
     * @throws WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue add(IWenyanValue other) throws WenyanThrowException;

    /**
     * Subtracts another value from this value.
     *
     * @param other the value to subtract from this value
     * @return the result of the subtraction
     * @throws WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue subtract(IWenyanValue other) throws WenyanThrowException;

    /**
     * Multiplies this value by another value.
     *
     * @param other the value to multiply this value by
     * @return the result of the multiplication
     * @throws WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue multiply(IWenyanValue other) throws WenyanThrowException;

    /**
     * Divides this value by another value.
     *
     * @param other the value to divide this value by
     * @return the result of the division
     * @throws WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue divide(IWenyanValue other) throws WenyanThrowException;
}
