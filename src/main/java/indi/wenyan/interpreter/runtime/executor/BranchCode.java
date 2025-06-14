package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;

import java.util.Objects;

public class BranchCode extends WenyanCode {
    private final Operation operation;
    private final Condition condition;

    public BranchCode(Condition c, Operation o) {
        super(name(c, o));
        this.condition = c;
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        boolean val = false;
        try {
            if (condition != Condition.NONE) {
                if (operation == Operation.POP) {
                    val = (boolean) runtime.processStack.pop()
                            .casting(WenyanType.BOOL).getValue();
                } else {
                    val = (boolean) runtime.processStack.peek()
                            .casting(WenyanType.BOOL).getValue();
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
