package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanBoolean;

import java.util.Objects;

public class BranchCode extends WenyanCode {
    private final Operation operation;
    private final Condition condition;

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

    private static String name(Condition c, Operation o) {
        StringBuilder sb = new StringBuilder();
        sb.append("BRANCH");
        if (Objects.requireNonNull(o) == Operation.POP) {
            sb.append("_POP");
        }
        switch (c) {
            case FALSE -> sb.append("_FALSE");
            case TRUE -> sb.append("_TRUE");
        }
        return sb.toString();
    }

    public enum Condition {
        FALSE,
        TRUE,
        NONE
    }

    public enum Operation {
        POP,
        NONE
    }
}
