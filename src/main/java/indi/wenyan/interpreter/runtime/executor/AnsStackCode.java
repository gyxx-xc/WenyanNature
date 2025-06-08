package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

import java.util.ArrayList;
import java.util.List;

public class AnsStackCode extends WenyanCode {
    private final Operation operation;

    public AnsStackCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case PUSH -> runtime.resultStack.push(runtime.processStack.pop());
            case POP -> runtime.processStack.push(runtime.resultStack.pop());
            case PEEK -> runtime.processStack.push(runtime.resultStack.peek());
            case PEEK_N -> {
                List<WenyanNativeValue> list = new ArrayList<>();
                for (int i = 0; i < args; i ++) {
                    list.add(runtime.resultStack.pop());
                    runtime.processStack.push(list.getLast());
                }
                for (var i : list) {
                    runtime.resultStack.push(i);
                }
            }
            case FLUSH -> runtime.resultStack.clear();
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) {
        if (operation == Operation.PEEK_N) {
            return args;
        }
        return super.getStep(args, thread);
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
