package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.*;
import net.minecraft.network.chat.Component;

public class VariableCode extends WenyanCode {
    private final Operation operation;

    public VariableCode(Operation o) {
        super(name(o));
        operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case LOAD -> {
                String id = runtime.bytecode.getIdentifier(args);
                WenyanValue value = thread.getGlobalVariable(id);
                if (value == null) {
                    throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + id);
                }
                runtime.processStack.push(value);
            }
            case STORE -> runtime.setVariable(runtime.bytecode.getIdentifier(args),
                    WenyanLeftValue.varOf(runtime.processStack.pop()));
            case SET_VALUE -> {
                WenyanValue value = runtime.processStack.pop();
                WenyanValue var =  runtime.processStack.pop();
                if (var instanceof WenyanLeftValue lv)
                    lv.setValue(value);
                else
                    throw new WenyanException(Component.translatable("error.wenyan_nature.set_value_to_non_left_value").getString());
            }
            case CAST -> {
                WenyanValue var = runtime.processStack.pop();
                try {
                    switch (args) {
                        case 1 -> var.as(WenyanInteger.TYPE);
                        case 2 -> var.as(WenyanDouble.TYPE);
                        case 3 -> var.as(WenyanBoolean.TYPE);
                        case 4 -> var.as(WenyanString.TYPE);
                        case 5 -> var.as(WenyanArrayObject.TYPE);
                        case 6 -> var.as(WenyanObject.TYPE);
                        case 7 -> var.as(WenyanObjectType.TYPE);
                        case 8 -> var.as(WenyanFunction.TYPE);
                        default -> throw new WenyanException(Component.translatable("error.wenyan_nature.invalid_data_type").getString());
                    }
                    runtime.processStack.push(var);
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
            }
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) {
        if (operation == Operation.LOAD) {
            return thread.runtimes.size();
        }
        return super.getStep(args, thread);
    }

    public enum Operation {
        LOAD,
        STORE,
        SET_VALUE,
        CAST
    }

    private static String name(Operation op) {
        return switch (op) {
            case LOAD -> "LOAD";
            case STORE -> "STORE";
            case SET_VALUE -> "SET_VAR";
            case CAST -> "CAST";
        };
    }
}
