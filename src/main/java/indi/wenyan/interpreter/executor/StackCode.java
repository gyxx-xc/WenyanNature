package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanProgram;

public class StackCode extends WenyanCode {
    private final Operation operation;

    public StackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.curThreads.cur();
        switch (operation) {
            case PUSH -> runtime.processStack.push(runtime.bytecode.getConst(args));
            case POP -> runtime.processStack.pop();
        }
    }

    public enum Operation {
        PUSH,
        POP
    }

    private static String name(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH";
            case POP -> "POP";
        };
    }
}
