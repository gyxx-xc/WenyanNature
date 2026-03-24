package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.ParsableType;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import org.jetbrains.annotations.UnknownNullability;

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
    public void exec(int arg, @UnknownNullability IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        switch (operation) {
            case LOAD -> {
                IWenyanValue value = runtime.getLocals().get(arg);
                runtime.pushReturnValue(value);
            }
            case LOAD_REF -> {
                IWenyanValue value = runtime.getReferences().get(arg);
                runtime.pushReturnValue(value);
            }
            case LOAD_GLOBAL -> {
                // cause this is bytecode level, use if to check still too slow
                String id = runtime.getBytecode().getIdentifier(arg);
                IWenyanValue value = thread.getGlobals().getAttribute(id);
                runtime.pushReturnValue(value);
            }
            case STORE -> {
                IWenyanValue value = runtime.getProcessStack().pop();
                runtime.setLocal(arg, WenyanLeftValue.varOf(value));
            }
            case SET_VALUE -> {
                IWenyanValue value = runtime.getProcessStack().pop();
                IWenyanValue variable = runtime.getProcessStack().pop();
                if (variable instanceof WenyanLeftValue lv) {
                    if (value == WenyanNull.NULL)
                        lv.setValue(WenyanNull.NULL);
                    else
                        lv.setValue(value.as(lv.type()));
                } else
                    throw new WenyanException(JudouExceptionText.SetValueToNonLeftValue.string());
            }
            case CAST -> {
                IWenyanValue value = runtime.getProcessStack().pop();
                runtime.pushReturnValue(value.as(ParsableType.values()[arg].getType()));
            }
        }
    }

    /**
     * Types of variable operations.
     */
    public enum Operation {
        LOAD,
        LOAD_REF,
        LOAD_GLOBAL,
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
            case LOAD_REF -> "LOAD_REF";
            case LOAD_GLOBAL -> "LOAD_GLOBAL";
            case STORE -> "STORE";
            case SET_VALUE -> "SET_VAR";
            case CAST -> "CAST";
        };
    }
}
