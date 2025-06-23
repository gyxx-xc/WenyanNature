package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanInteger;
import indi.wenyan.interpreter.structure.values.WenyanIterator;
import indi.wenyan.interpreter.structure.values.WenyanValue;

import java.util.Iterator;

public class ForCode extends WenyanCode {
    private final Operation operation;
    public ForCode(Operation operation) {
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case FOR_ITER -> {
                Iterator<?> iter = null;
                try {
                    iter = runtime.processStack.peek().as(WenyanIterator.TYPE).value();
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
                if (iter.hasNext()) {
                    runtime.processStack.push((WenyanValue) iter.next());
                } else {
                    runtime.processStack.pop();
                    runtime.programCounter = runtime.bytecode.getLabel(args);
                    runtime.PCFlag = true;
                }
            }
            case FOR_NUM -> {
                WenyanValue value = runtime.processStack.pop();
                try {
                    int num = value.as(WenyanInteger.TYPE).value();
                    if (num > 0) {
                        runtime.processStack.push(new WenyanInteger(num - 1));
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

    public enum Operation {
        FOR_ITER,
        FOR_NUM
    }

    public static String name(Operation op) {
        return switch (op) {
            case FOR_ITER -> "FOR_ITER";
            case FOR_NUM -> "FOR_NUM";
        };
    }
}
