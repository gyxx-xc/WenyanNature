package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.api.values.primitive.WenyanBoolean;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

/**
 * Handles conditional branching operations in the Wenyan interpreter.
 */
public enum BranchCode {
    ;

    static void branchTrue(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        assert runtime.getProcessStack().peek() != null;
        boolean value = runtime.getProcessStack().peek()
                .as(WenyanBoolean.TYPE).value();
        if (value) {
            runtime.setProgramCounter(arg);
            runtime.setPCFlag(true);
        }
    }

    static void branchFalse(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        assert runtime.getProcessStack().peek() != null;
        boolean value = runtime.getProcessStack().peek()
                .as(WenyanBoolean.TYPE).value();
        if (!value) {
            runtime.setProgramCounter(arg);
            runtime.setPCFlag(true);
        }
    }

    static void branchPopFalse(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        boolean value = runtime.getProcessStack().pop()
                .as(WenyanBoolean.TYPE).value();
        if (!value) {
            runtime.setProgramCounter(arg);
            runtime.setPCFlag(true);
        }
    }

    static void branch(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.setProgramCounter(arg);
        runtime.setPCFlag(true);
    }
}
