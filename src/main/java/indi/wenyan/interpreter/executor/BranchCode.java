package indi.wenyan.interpreter.executor;

public class BranchCode extends WenyanCode{
    private final Operation operation;
    private final Condition condition;

    protected BranchCode(Condition c, Operation o) {
        super(name(c, o));
        this.condition = c;
        this.operation = o;
    }

    @Override
    public void exec(int args) {

    }

    private static String name(Condition c, Operation o) {
        StringBuilder sb = new StringBuilder();
        sb.append("BRANCH");
        switch (o) { // remain for future use
            case POP -> sb.append("_POP");
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
