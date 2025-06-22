package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.WenyanInteger;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNativeValue1;
import net.minecraft.network.chat.Component;

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
                WenyanValue value = runtime.processStack.peek();
                if (value.getValue() instanceof Iterator<?> iter) {
                    if (iter.hasNext()) {
                        runtime.processStack.push((WenyanValue) iter.next());
                    } else {
                        runtime.processStack.pop();
                        runtime.programCounter = runtime.bytecode.getLabel(args);
                        runtime.PCFlag = true;
                    }
                } else {
                    throw new WenyanException(Component.translatable("error.wenyan_nature.for_iter").getString());
                }
            }
            case FOR_NUM -> {
                WenyanValue value = runtime.processStack.pop();
                try {
                    int num = value.as(WenyanInteger.TYPE).value;
                    if (num > 0) {
                        runtime.processStack.push(new WenyanNativeValue1(WenyanInteger.TYPE, num - 1, true));
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
