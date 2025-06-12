package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import net.minecraft.network.chat.Component;

public class VariableCode extends WenyanCode {
    private final Operation operation;

    public VariableCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case LOAD -> {
                String id = runtime.bytecode.getIdentifier(args);
                WenyanNativeValue value = thread.getGlobalVariable(id);
                if (value == null) {
                    throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString() + id);
                }
                runtime.processStack.push(value);
            }
            case STORE -> runtime.setVariable(runtime.bytecode.getIdentifier(args), WenyanNativeValue.varOf(runtime.processStack.pop()));
            case SET_VALUE -> {
                WenyanNativeValue value = runtime.processStack.pop();
                WenyanNativeValue var =  runtime.processStack.pop();
                if (var.isConst())
                    throw new WenyanException(Component.translatable("error.wenyan_nature.cannot_assign_to_constant").getString());
                try {
                    var.setValue(value.casting(var.type()).getValue());
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
            }
            case CAST -> {
                WenyanNativeValue var = runtime.processStack.pop();
                try {
                    switch (args) {
                        case 1 -> var.casting(WenyanType.INT);
                        case 2 -> var.casting(WenyanType.DOUBLE);
                        case 3 -> var.casting(WenyanType.BOOL);
                        case 4 -> var.casting(WenyanType.STRING);
                        case 5 -> var.casting(WenyanType.LIST);
                        case 6 -> var.casting(WenyanType.OBJECT);
                        case 7 -> var.casting(WenyanType.OBJECT_TYPE);
                        case 8 -> var.casting(WenyanType.FUNCTION);
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
