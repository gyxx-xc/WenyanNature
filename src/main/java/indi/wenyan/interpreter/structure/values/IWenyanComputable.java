package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;

/**
 * Interface for Wenyan values that support basic arithmetic operations.
 */
public interface IWenyanComputable extends IWenyanValue {
    /**
     * Adds this value to another value.
     *
     * @param other the value to add to this value
     * @return the result of the addition
     * @throws WenyanException.WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue add(IWenyanValue other) throws WenyanException.WenyanThrowException;

    /**
     * Subtracts another value from this value.
     *
     * @param other the value to subtract from this value
     * @return the result of the subtraction
     * @throws WenyanException.WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue subtract(IWenyanValue other) throws WenyanException.WenyanThrowException;

    /**
     * Multiplies this value by another value.
     *
     * @param other the value to multiply this value by
     * @return the result of the multiplication
     * @throws WenyanException.WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue multiply(IWenyanValue other) throws WenyanException.WenyanThrowException;

    /**
     * Divides this value by another value.
     *
     * @param other the value to divide this value by
     * @return the result of the division
     * @throws WenyanException.WenyanThrowException if the operation is not supported for these types
     */
    IWenyanValue divide(IWenyanValue other) throws WenyanException.WenyanThrowException;
}
