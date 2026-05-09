package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.runtime.IWenyanRunner;

/**
 * Base class for all executable code in the Wenyan interpreter.
 */
public interface WenyanCode {
    /**
     * Executes this code with the given arguments and thread context.
     *
     * @param arg The arguments for execution
     * @param thread The thread context
     */
    void exec(int arg, IWenyanRunner thread) throws WenyanException;
}
