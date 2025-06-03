package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanProgram;

public class ReturnCode extends WenyanCode {
    public ReturnCode() {
        super("RETURN");
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.curThreads.cur();
        program.curThreads.ret();
        if (!runtime.noReturnFlag)
            program.curThreads.cur().processStack.push(runtime.processStack.pop());
    }
}
