package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface IFrameManager<T> {
    /**
     * Adds a runtime environment to the top of the stack.
     *
     * @param runtime The runtime to add
     */
    void call(T runtime);

    /**
     * Removes the top runtime environment from the stack.
     */
    void ret() throws WenyanUnreachedException;

    T getCurrentRuntime();

    IGlobalResolver getGlobals();
}
