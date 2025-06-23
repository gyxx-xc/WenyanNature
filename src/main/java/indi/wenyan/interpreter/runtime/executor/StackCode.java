package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.WenyanLeftValue;

public class StackCode extends WenyanCode {
    private final Operation operation;

    public StackCode(Operation o) {
        super(name(o));
        operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case PUSH -> runtime.processStack.push(WenyanLeftValue.varOf(runtime.bytecode.getConst(args)));
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
