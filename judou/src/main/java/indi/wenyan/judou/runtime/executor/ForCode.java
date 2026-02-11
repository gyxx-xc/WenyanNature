package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.WenyanRuntime;
import indi.wenyan.judou.runtime.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.warper.WenyanIterator;
import indi.wenyan.judou.utils.WenyanValues;

import java.util.Iterator;

/**
 * Handles loop operations in the Wenyan interpreter.
 */
public class ForCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new ForCode with the specified operation.
     *
     * @param operation The operation to perform in the loop
     */
    public ForCode(Operation operation) {
        super(opName(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) throws WenyanThrowException {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case FOR_ITER -> {
                Iterator<?> iter;
                try {
                    iter = runtime.getProcessStack().peek().as(WenyanIterator.TYPE).value();
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
                if (iter.hasNext()) {
                    runtime.pushReturnValue((IWenyanValue) iter.next());
                } else {
                    runtime.getProcessStack().pop();
                    runtime.programCounter = runtime.getBytecode().getLabel(args);
                    runtime.PCFlag = true;
                }
            }
            case FOR_NUM -> {
                IWenyanValue value = runtime.getProcessStack().pop();
                int num = value.as(WenyanInteger.TYPE).value();
                if (num > 0) {
                    runtime.pushReturnValue(WenyanValues.of((long) num - 1));
                } else {
                    runtime.programCounter = runtime.getBytecode().getLabel(args);
                    runtime.PCFlag = true;
                }
            }
        }
    }

    /**
     * Operations for different types of loops.
     */
    public enum Operation {
        FOR_ITER,
        FOR_NUM
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    public static String opName(Operation op) {
        return switch (op) {
            case FOR_ITER -> "FOR_ITER";
            case FOR_NUM -> "FOR_NUM";
        };
    }
}
