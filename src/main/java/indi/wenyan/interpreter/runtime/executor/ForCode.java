package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
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
                WenyanNativeValue value = runtime.processStack.peek();
                if (value.getValue() instanceof Iterator<?> iter) {
                    if (iter.hasNext()) {
                        runtime.processStack.push((WenyanNativeValue) iter.next());
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
                WenyanNativeValue value = runtime.processStack.pop();
                if (value.type() == WenyanType.INT) {
                    int num = (int) value.getValue();
                    if (num > 0) {
                        runtime.processStack.push(new WenyanNativeValue(WenyanType.INT, num - 1, true));
                    } else {
                        runtime.programCounter = runtime.bytecode.getLabel(args);
                        runtime.PCFlag = true;
                    }
                } else {
                    throw new WenyanException(Component.translatable("error.wenyan_nature.for_num").getString());
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
