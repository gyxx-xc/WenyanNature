package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.warper.WenyanIterator;
import indi.wenyan.interpreter.utils.WenyanValues;

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
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case FOR_ITER -> {
                Iterator<?> iter;
                try {
                    iter = runtime.processStack.peek().as(WenyanIterator.TYPE).value();
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
                if (iter.hasNext()) {
                    runtime.processStack.push((IWenyanValue) iter.next());
                } else {
                    runtime.processStack.pop();
                    runtime.programCounter = runtime.bytecode.getLabel(args);
                    runtime.PCFlag = true;
                }
            }
            case FOR_NUM -> {
                IWenyanValue value = runtime.processStack.pop();
                try {
                    int num = value.as(WenyanInteger.TYPE).value();
                    if (num > 0) {
                        runtime.processStack.push(WenyanValues.of(num - 1));
                    } else {
                        runtime.programCounter = runtime.bytecode.getLabel(args);
                        runtime.PCFlag = true;
                    }
                } catch (WenyanException.WenyanThrowException e){
                    throw new WenyanException(e.getMessage());
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
    public static String name(Operation op) {
        return switch (op) {
            case FOR_ITER -> "FOR_ITER";
            case FOR_NUM -> "FOR_NUM";
        };
    }
}
