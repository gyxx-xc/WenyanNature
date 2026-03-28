package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;

/**
 * Handles function return operations in the Wenyan interpreter.
 */
public enum ReturnCode {
    ;

    static void ret(IWenyanRunner thread) throws WenyanException {
        WenyanFrame currentRuntime = thread.getCurrentRuntime();
        currentRuntime.getReturnBehavior().onReturn(thread, currentRuntime.getProcessStack().pop());
    }
}
