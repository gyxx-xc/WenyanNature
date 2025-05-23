package indi.wenyan.interpreter.executor;

public class StackCode extends WenyanCode {
    private final Operation operation;

    public StackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args) {
    }

    public enum Operation {
        PUSH,
        POP,
        TOP,
        CLEAR,
        SIZE,
        IS_EMPTY
    }

    private static String name(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH";
            case POP -> "POP";
            case TOP -> "TOP";
            case CLEAR -> "CLEAR";
            case SIZE -> "SIZE";
            case IS_EMPTY -> "IS_EMPTY";
        };
    }
}
