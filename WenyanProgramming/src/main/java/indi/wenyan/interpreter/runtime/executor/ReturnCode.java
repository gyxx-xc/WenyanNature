package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;

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
            thread.currentRuntime().processStack.push(runtime.processStack.pop());
    }
}
