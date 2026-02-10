package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.*;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.structure.values.warper.WenyanList;
import net.minecraft.network.chat.Component;

/**
 * Handles variable operations in the Wenyan interpreter.
 */
public class VariableCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new VariableCode with the specified operation.
     *
     * @param o The operation to perform on variables
     */
    public VariableCode(Operation o) {
        super(opName(o));
        operation = o;
    }

    @Override
    public void exec(int args, WenyanThread thread) throws WenyanThrowException {
        WenyanRuntime runtime = thread.currentRuntime();
        switch (operation) {
            case LOAD -> {
                if (runtime.bytecode == null) throw new WenyanException.WenyanUnreachedException();
                String id = runtime.bytecode.getIdentifier(args);
                IWenyanValue value = thread.getGlobalVariable(id);
                if (value == null) {
                    throw new WenyanException(Component.translatable("error.wenyan_programming.variable_not_found_").getString() + id);
                }
                runtime.processStack.push(value);
            }
            case STORE -> {
                if (runtime.bytecode == null) throw new WenyanException.WenyanUnreachedException();
                runtime.setVariable(runtime.bytecode.getIdentifier(args),
                        WenyanLeftValue.varOf(runtime.processStack.pop()));
            }
            case SET_VALUE -> {
                IWenyanValue value = runtime.processStack.pop();
                IWenyanValue variable = runtime.processStack.pop();
                if (variable instanceof WenyanLeftValue lv) {
                    if (value.is(WenyanNull.TYPE))
                        lv.setValue(WenyanNull.NULL);
                    else
                        lv.setValue(value.as(lv.type()));

                } else
                    throw new WenyanException(Component.translatable("error.wenyan_programming.set_value_to_non_left_value").getString());
            }
            case CAST -> {
                IWenyanValue value = runtime.processStack.pop();
                try {
                    // TODO: use const with TYPE in bytecode?
                    var castedValue = switch (args) {
                        case 1 -> value.as(WenyanInteger.TYPE);
                        case 2 -> value.as(WenyanDouble.TYPE);
                        case 3 -> value.as(WenyanBoolean.TYPE);
                        case 4 -> value.as(WenyanString.TYPE);
                        case 5 -> value.as(WenyanList.TYPE);
                        case 6 -> value.as(IWenyanObject.TYPE);
                        case 7 -> value.as(IWenyanObjectType.TYPE);
                        case 8 -> value.as(IWenyanFunction.TYPE);
                        default ->
                                throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_data_type").getString());
                    };
                    runtime.processStack.push(castedValue);
                } catch (WenyanException.WenyanTypeException e) {
                    throw new WenyanException(e.getMessage());
                }
            }
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) throws WenyanThrowException {
        if (operation == Operation.LOAD) {
            return thread.runtimes.size();
        }
        return super.getStep(args, thread);
    }

    /**
     * Types of variable operations.
     */
    public enum Operation {
        LOAD,
        STORE,
        SET_VALUE,
        CAST
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String opName(Operation op) {
        return switch (op) {
            case LOAD -> "LOAD";
            case STORE -> "STORE";
            case SET_VALUE -> "SET_VAR";
            case CAST -> "CAST";
        };
    }
}
