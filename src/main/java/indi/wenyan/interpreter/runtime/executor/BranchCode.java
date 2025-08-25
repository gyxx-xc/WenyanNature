package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;

/**
 * Handles conditional branching operations in the Wenyan interpreter.
 */
public class BranchCode extends WenyanCode {
    private final Operation operation;
    private final Condition condition;

    /**
     * Creates a new BranchCode with the specified condition and operation.
     *
     * @param c The condition for branching
     * @param o The operation to perform
     */
    public BranchCode(Condition c, Operation o) {
        super(name(c, o));
        condition = c;
        operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        boolean val = false;
        try {
            if (condition != Condition.NONE) {
                if (operation == Operation.POP) {
                    val = runtime.processStack.pop()
                            .as(WenyanBoolean.TYPE).value();
                } else {
                    val = runtime.processStack.peek()
                            .as(WenyanBoolean.TYPE).value();
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }

        boolean jump = switch (condition) {
                case FALSE -> !(val);
                case TRUE -> val;
                case NONE -> true;
            };
        if (jump) {
            runtime.programCounter = runtime.bytecode.getLabel(args);
            runtime.PCFlag = true;
        }
    }

    /**
     * Generates the name of the code based on the condition and operation.
     *
     * @param c The condition
     * @param o The operation
     * @return The name of the code
     */
    private static String name(Condition c, Operation o) {
        StringBuilder sb = new StringBuilder();
        sb.append("BRANCH");
        if (o == Operation.POP) {
            sb.append("_POP");
        }
        switch (c) {
            case FALSE -> sb.append("_FALSE");
            case TRUE -> sb.append("_TRUE");
        }
        return sb.toString();
    }

    /**
     * Conditions for branch execution.
     */
    public enum Condition {
        FALSE,
        TRUE,
        NONE
    }

    /**
     * Operations that can be performed during branching.
     */
    public enum Operation {
        POP,
        NONE
    }
}
