package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanThrowException;

/**
 * Interface for Wenyan values that wrap a Java value.
 *
 * @param <T> the type of the wrapped Java value
 */
public interface IWenyanWarperValue<T> extends IWenyanValue {
    /**
     * Gets the wrapped Java value.
     *
     * @return the wrapped value
     */
    T value() throws WenyanThrowException;
}
