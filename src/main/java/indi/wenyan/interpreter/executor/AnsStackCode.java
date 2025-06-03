package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.utils.WenyanProgram;

public class AnsStackCode extends WenyanCode {
    private final Operation operation;

    public AnsStackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.curThreads.cur();
        switch (operation) {
            case PUSH -> runtime.resultStack.push(runtime.processStack.pop());
            case POP -> runtime.processStack.push(runtime.resultStack.pop());
            case PEEK -> runtime.processStack.push(runtime.resultStack.peek());
            case PEEK_N -> {
                for (int i = 0; i < args; i ++)
                    runtime.processStack.push(runtime.
                            resultStack.get(runtime.resultStack.size()-i-1));
            }
            case FLUSH -> runtime.resultStack.clear();
        }
    }

    @Override
    public int getStep(int args, WenyanProgram program) {
        if (operation == Operation.PEEK_N) {
            return args;
        }
        return super.getStep(args, program);
    }

    public enum Operation {
        PUSH,
        POP,
        PEEK,
        PEEK_N,
        FLUSH
    }

    private static String name(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH_ANS";
            case POP -> "POP_ANS";
            case PEEK -> "PEEK_ANS";
            case PEEK_N -> "PEEK_ANS_N";
            case FLUSH -> "FLUSH_ANS";
        };
    }
}
