package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanCode;

public class StackCode extends WenyanCode {
    private final Operation operation;

    public StackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanRuntime runtime) {
        switch (operation) {
            case PUSH -> runtime.processStack.push(runtime.getConstant(args));
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
