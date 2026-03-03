package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
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
    public void exec(int arg, @UnknownNullability WenyanRunner thread) {
        WenyanRuntime runtime = thread.getCurrentRuntime();
        thread.ret();
        if (!runtime.noReturnFlag && runtime.getReturnRuntime() != null)
            runtime.getReturnRuntime().pushReturnValue(runtime.getProcessStack().pop());
    }
}
