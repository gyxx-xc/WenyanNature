package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanUnreachedException;

/**
 * Handles stack operations in the Wenyan interpreter.
 */
public enum StackCode {
    ;

    static void popStack(IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.getProcessStack().pop();
    }

    static void pushStack(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.pushReturnValue(runtime.getBytecode().getConst(arg));
    }
}
