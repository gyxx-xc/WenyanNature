package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.WenyanCode;
import net.minecraft.network.chat.Component;

public class VariableCode extends WenyanCode {
    private final Operation operation;

    public VariableCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanRuntime runtime) {
        switch (operation) {
            case LOAD -> runtime.processStack.push(runtime.getVariable(runtime.bytecode.getIdentifier(args)));
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
                        default -> throw new WenyanException(Component.translatable("error.wenyan_nature.invalid_data_type").getString());
                    }
                    runtime.processStack.push(var);
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
            }
        }
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
