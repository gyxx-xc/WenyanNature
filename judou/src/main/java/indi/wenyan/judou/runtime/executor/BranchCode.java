package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;

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
            runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
            runtime.setPCFlag(true);
        }
    }

    static void branchFalse(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        assert runtime.getProcessStack().peek() != null;
        boolean value = runtime.getProcessStack().peek()
                .as(WenyanBoolean.TYPE).value();
        if (!value) {
            runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
            runtime.setPCFlag(true);
        }
    }

    static void branchPopFalse(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        boolean value = runtime.getProcessStack().pop()
                .as(WenyanBoolean.TYPE).value();
        if (!value) {
            runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
            runtime.setPCFlag(true);
        }
    }

    static void branch(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
        runtime.setPCFlag(true);
    }
}
