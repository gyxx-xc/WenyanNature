package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanThread;

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
