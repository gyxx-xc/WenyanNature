package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanProgram;
import net.minecraft.network.chat.Component;

import java.util.Stack;

public class VariableCode extends WenyanCode {
    private final Operation operation;

    public VariableCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.runtimes.peek();
        switch (operation) {
            case LOAD -> {
                Stack<WenyanRuntime> runtimeStack = program.runtimes;
                String id = runtime.bytecode.getIdentifier(args);
                WenyanValue value = null;
                for (int i = runtimeStack.size()-1; i >= 0; i --) {
                    if (runtimeStack.get(i).variables.containsKey(id)) {
                        value = runtimeStack.get(i).variables.get(id);
                        break;
                    }
                }
                if (value == null)
                    throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString()+id);
                runtime.processStack.push(value);
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
    public int getStep(int args, WenyanProgram program) {
        if (operation == Operation.LOAD) {
            return program.runtimes.size();
        }
        return super.getStep(args, program);
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
