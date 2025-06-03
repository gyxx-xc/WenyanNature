package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanProgram;

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
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.runtimes.cur();
        boolean val = false;
        try {
            if (condition != Condition.NONE) {
                if (operation == Operation.POP) {
                    val = (boolean) runtime.processStack.pop()
                            .casting(WenyanValue.Type.BOOL).getValue();
                } else {
                    val = (boolean) runtime.processStack.peek()
                            .casting(WenyanValue.Type.BOOL).getValue();
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
