package indi.wenyan.judou.runtime;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import org.jetbrains.annotations.Nullable;

public interface IFrameManager<T> {
    /**
     * Adds a runtime environment to the top of the stack.
     *
     * @param runtime The runtime to add
     */
    void call(T runtime) throws WenyanException;

    /**
     * Removes the top runtime environment from the stack.
     */
    void ret() throws WenyanUnreachedException;

    // TODO: rename these two function
    T getCurrentRuntime() throws WenyanUnreachedException;

    @Nullable T getNullableCurrentRuntime();
}
