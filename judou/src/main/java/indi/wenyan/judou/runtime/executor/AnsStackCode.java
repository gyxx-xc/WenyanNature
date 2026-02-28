package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles operations related to the result stack in the Wenyan interpreter.
 */
public class AnsStackCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new AnsStackCode with the specified operation.
     *
     * @param o The operation to perform on the result stack
     */
    public AnsStackCode(Operation o) {
        super(opName(o));
        operation = o;
    }

    @Override
    public void exec(int arg, @UnknownNullability WenyanThread thread) throws WenyanException {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case PUSH -> runtime.getResultStack().push(runtime.getProcessStack().pop());
            case POP -> runtime.pushReturnValue(runtime.getResultStack().pop());
            case PEEK -> runtime.pushReturnValue(runtime.getResultStack().peek());
            case PEEK_N -> {
                List<IWenyanValue> list = new ArrayList<>();
                for (int i = 0; i < arg; i++) {
                    list.add(runtime.getResultStack().pop());
                    runtime.pushReturnValue(list.getLast());
                }
                for (var i : list) {
                    runtime.getResultStack().push(i);
                }
            }
            case FLUSH -> runtime.getResultStack().clear();
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) throws WenyanException {
        if (operation == Operation.PEEK_N) {
            return args;
        }
        return super.getStep(args, thread);
    }

    /**
     * Operations that can be performed on the result stack.
     */
    public enum Operation {
        PUSH,
        POP,
        PEEK,
        PEEK_N,
        FLUSH
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String opName(Operation op) {
        return switch (op) {
            case PUSH -> "PUSH_ANS";
            case POP -> "POP_ANS";
            case PEEK -> "PEEK_ANS";
            case PEEK_N -> "PEEK_ANS_N";
            case FLUSH -> "FLUSH_ANS";
        };
    }
}
