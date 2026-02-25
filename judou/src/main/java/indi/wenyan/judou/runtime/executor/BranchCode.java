package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Handles conditional branching operations in the Wenyan interpreter.
 */
public class BranchCode extends WenyanCode {
    private final Condition condition;

    /**
     * Creates a new BranchCode with the specified condition and operation.
     *
     * @param c The condition for branching
     * @param o The operation to perform
     */
    public BranchCode(Condition c) {
        super(opName(c));
        condition = c;
    }

    @Override
    public void exec(int args, @UnknownNullability WenyanThread thread) throws WenyanException {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (condition) {
            case NONE -> {
                runtime.programCounter = runtime.getBytecode().getLabel(args);
                runtime.PCFlag = true;
            }
            case POP_FALSE -> {
                boolean value = runtime.getProcessStack().pop()
                        .as(WenyanBoolean.TYPE).value();
                if (!value) {
                    runtime.programCounter = runtime.getBytecode().getLabel(args);
                    runtime.PCFlag = true;
                }
            }
            case FALSE -> {
                boolean value = runtime.getProcessStack().peek()
                        .as(WenyanBoolean.TYPE).value();
                if (!value) {
                    runtime.programCounter = runtime.getBytecode().getLabel(args);
                    runtime.PCFlag = true;
                }
            }
            case TRUE -> {
                boolean value = runtime.getProcessStack().peek()
                        .as(WenyanBoolean.TYPE).value();
                if (value) {
                    runtime.programCounter = runtime.getBytecode().getLabel(args);
                    runtime.PCFlag = true;
                }
            }
        }
    }

    /**
     * Generates the name of the code based on the condition and operation.
     *
     * @param c The condition
     * @param o The operation
     * @return The name of the code
     */
    private static String opName(Condition c) {
        StringBuilder sb = new StringBuilder();
        sb.append("BRANCH");
        switch (c) {
            case POP_FALSE -> sb.append("_POP_FALSE");
            case FALSE -> sb.append("_FALSE");
            case TRUE -> sb.append("_TRUE");
            case NONE -> {
            }
        }
        return sb.toString();
    }

    /**
     * Conditions for branch execution.
     */
    public enum Condition {
        POP_FALSE,
        FALSE,
        TRUE,
        NONE
    }
}
