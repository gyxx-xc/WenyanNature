package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

/// Handles function return operations in the Wenyan interpreter.
public enum ReturnCode {
    ;

    static void ret(IWenyanRunner thread) throws WenyanException {
        WenyanFrame currentRuntime = thread.getCurrentRuntime();
        currentRuntime.getReturnBehavior().onReturn(thread, currentRuntime.getProcessStack().pop());
    }
}
