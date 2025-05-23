package indi.wenyan.interpreter.executor;

public class FunctionCode extends WenyanCode{
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args) {

    }

    public enum Operation {
        CALL,
        RETURN
    }

    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case RETURN -> "RETURN";
        };
    }

}
