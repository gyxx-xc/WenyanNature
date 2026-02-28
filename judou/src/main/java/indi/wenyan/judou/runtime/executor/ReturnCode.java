package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import org.jetbrains.annotations.UnknownNullability;

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
    public void exec(int arg, @UnknownNullability WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        thread.ret();
        if (!runtime.noReturnFlag)
            thread.currentRuntime().pushReturnValue(runtime.getProcessStack().pop());
    }
}
