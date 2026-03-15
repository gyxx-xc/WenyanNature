package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;

/**
 * Base class for all executable code in the Wenyan interpreter.
 */
public abstract class WenyanCode {
    /** The name of this code operation */
    public final String name;

    /**
     * Creates a new WenyanCode with the specified name.
     *
     * @param name The name of this code
     */
    protected WenyanCode(String name) {
        this.name = name;
    }

    /**
     * Executes this code with the given arguments and thread context.
     *
     * @param arg The arguments for execution
     * @param thread The thread context
     */
    public abstract void exec(int arg, IWenyanRunner thread) throws WenyanException;

    @Override
    public String toString() {
        return name;
    }
}
