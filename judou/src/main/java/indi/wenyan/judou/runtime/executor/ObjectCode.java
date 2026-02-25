package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.*;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObject;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinObjectType;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Handles object-related operations in the Wenyan interpreter.
 */
public class ObjectCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new ObjectCode with the specified operation.
     *
     * @param operation The operation to perform on objects
     */
    public ObjectCode(Operation operation) {
        super(opName(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, @UnknownNullability WenyanThread thread) throws WenyanException {
        WenyanRuntime runtime = thread.currentRuntime();
        String id = runtime.getBytecode().getIdentifier(args);
        switch (operation) {
            case ATTR, ATTR_REMAIN -> {
                IWenyanValue attr;
                IWenyanValue value = operation == Operation.ATTR ?
                        runtime.getProcessStack().pop() : runtime.getProcessStack().peek();
                if (value.is(IWenyanObjectType.TYPE)) {
                    IWenyanObjectType object = value.as(IWenyanObjectType.TYPE);
                    attr = object.getAttribute(id);
                } else {
                    IWenyanObject object = value.as(IWenyanObject.TYPE);
                    attr = object.getAttribute(id);
                }
                runtime.pushReturnValue(attr);
            }
            case STORE_STATIC_ATTR -> {
                IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                type.addStaticVariable(id, value);
            }
            case STORE_FUNCTION_ATTR -> {
                IWenyanValue value = runtime.getProcessStack().pop();
                WenyanBuiltinObjectType type = runtime.getProcessStack().peek().as(WenyanBuiltinObjectType.TYPE);
                type.addFunction(id, value);
            }
            case STORE_ATTR -> {
                // currently only used at define (mzy SELF ZHI STRING)
                WenyanBuiltinObject self = runtime.getProcessStack().pop().as(WenyanBuiltinObject.TYPE);
                IWenyanValue value = WenyanLeftValue.varOf(runtime.getProcessStack().pop());
                self.createAttribute(id, value);
            }
            case CREATE_TYPE -> {
                var parent = runtime.getProcessStack().pop();
                IWenyanValue type;
                if (parent.is(WenyanNull.TYPE))
                    type = new WenyanBuiltinObjectType(null);
                else
                    type = new WenyanBuiltinObjectType(parent
                            .as(WenyanBuiltinObjectType.TYPE));
                runtime.pushReturnValue(type);
            }
        }
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String opName(Operation op) {
        return switch (op) {
            case ATTR -> "LOAD_ATTR";
            case ATTR_REMAIN -> "LOAD_ATTR_REMAIN";
            case STORE_STATIC_ATTR -> "STORE_STATIC_ATTR";
            case STORE_FUNCTION_ATTR -> "STORE_FUNCTION_ATTR";
            case STORE_ATTR -> "STORE_ATTR";
            case CREATE_TYPE -> "CREATE_TYPE";
        };
    }

    /**
     * Operations that can be performed on objects.
     */
    public enum Operation {
        ATTR,
        ATTR_REMAIN,
        STORE_ATTR,
        STORE_STATIC_ATTR,
        STORE_FUNCTION_ATTR,
        CREATE_TYPE
    }
}
