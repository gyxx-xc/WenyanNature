package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.WenyanRuntime;
import indi.wenyan.judou.runtime.WenyanThread;

/**
 * Handles function return operations in the Wenyan interpreter.
 */
public class ReturnCode extends WenyanCode {

    /**
     * Creates a new ReturnCode.
     */
    public ReturnCode() {
        super("RETURN");
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        thread.ret();
        if (!runtime.noReturnFlag)
            thread.currentRuntime().pushReturnValue(runtime.getProcessStack().pop());
    }
}
