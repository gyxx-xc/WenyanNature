package indi.wenyan.interpreter.executor;

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
                runtime.processStack.push(thread.getGlobalVariable(id));
            }
            case STORE -> runtime.setVariable(runtime.bytecode.getIdentifier(args), WenyanValue.varOf(runtime.processStack.pop()));
            case SET_VALUE -> {
                WenyanValue value = runtime.processStack.pop();
                WenyanValue var =  runtime.processStack.pop();
                if (var.isConst())
                    throw new WenyanException(Component.translatable("error.wenyan_nature.cannot_assign_to_constant").getString());
                try {
                    var.setValue(value.casting(var.getType()).getValue());
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
            }
            case CAST -> {
                WenyanValue var = runtime.processStack.pop();
                try {
                    switch (args) {
                        case 1 -> var.casting(WenyanValue.Type.INT);
                        case 2 -> var.casting(WenyanValue.Type.DOUBLE);
                        case 3 -> var.casting(WenyanValue.Type.BOOL);
                        case 4 -> var.casting(WenyanValue.Type.STRING);
                        case 5 -> var.casting(WenyanValue.Type.LIST);
                        case 6 -> var.casting(WenyanValue.Type.OBJECT);
                        case 7 -> var.casting(WenyanValue.Type.OBJECT_TYPE);
                        case 8 -> var.casting(WenyanValue.Type.FUNCTION);
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
