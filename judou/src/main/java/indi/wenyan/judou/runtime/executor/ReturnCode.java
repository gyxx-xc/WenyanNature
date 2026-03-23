package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
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
    public void exec(int arg, @UnknownNullability IWenyanRunner thread) throws WenyanException {
        WenyanFrame currentRuntime = thread.getCurrentRuntime();
        currentRuntime.getReturnBehavior().onReturn(thread, currentRuntime.getProcessStack().pop());
    }
}
