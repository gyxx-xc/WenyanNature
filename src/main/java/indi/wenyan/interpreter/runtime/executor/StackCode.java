package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;

public class StackCode extends WenyanCode {
    private final Operation operation;

    public StackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
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
