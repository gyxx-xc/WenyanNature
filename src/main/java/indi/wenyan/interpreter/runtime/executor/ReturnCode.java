package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;

public class ReturnCode extends WenyanCode {
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
