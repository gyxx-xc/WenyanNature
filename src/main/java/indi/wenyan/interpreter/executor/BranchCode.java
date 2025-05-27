package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.WenyanCode;

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
    public void exec(int args, WenyanRuntime runtime) {
        if (operation == Operation.POP) {
            runtime.processStack.pop();
        }
        boolean jump;
        try {
            jump = switch (condition) {
                case FALSE -> !((boolean) runtime.processStack.pop().casting(WenyanValue.Type.BOOL).getValue());
                case TRUE -> (boolean) runtime.processStack.pop().casting(WenyanValue.Type.BOOL).getValue();
                case NONE -> true;
            };
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (jump) {
            runtime.programCounter = runtime.getLabel(args);
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
