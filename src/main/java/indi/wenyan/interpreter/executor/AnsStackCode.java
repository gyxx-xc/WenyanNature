package indi.wenyan.interpreter.executor;

public class AnsStackCode extends WenyanCode{

    private final Operation operation;

    public AnsStackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args) {

    }

    public enum Operation {
        PUSH,
        POP,
        PEEK,
        FLUSH
    }

    private static String name(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH_ANS";
            case POP -> "POP_ANS";
            case PEEK -> "PEEK_ANS";
            case FLUSH -> "FLUSH_ANS";
        };
    }
}
