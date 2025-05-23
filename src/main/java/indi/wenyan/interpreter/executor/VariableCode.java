package indi.wenyan.interpreter.executor;

public class VariableCode extends WenyanCode {
    private final Operation operation;

    public VariableCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args) {

    }

    public enum Operation {
        LOAD,
        STORE,
        SET_VALUE,
        CAST
    }

    private static String name(Operation op) {
        return switch (op) {
            case LOAD -> "LOAD";
            case STORE -> "STORE";
            case SET_VALUE -> "SET_VALUE";
            case CAST -> "CAST";
        };
    }
}
