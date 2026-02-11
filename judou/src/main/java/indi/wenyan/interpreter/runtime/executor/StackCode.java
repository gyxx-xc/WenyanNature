package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;

/**
 * Handles stack operations in the Wenyan interpreter.
 */
public class StackCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new StackCode with the specified operation.
     *
     * @param o The operation to perform on the stack
     */
    public StackCode(Operation o) {
        super(opName(o));
        operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case PUSH -> runtime.pushReturnValue(runtime.getBytecode().getConst(args));
            case POP -> runtime.getProcessStack().pop();
        }
    }

    /**
     * Types of stack operations.
     */
    public enum Operation {
        PUSH,
        POP
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String opName(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH";
            case POP -> "POP";
        };
    }
}
