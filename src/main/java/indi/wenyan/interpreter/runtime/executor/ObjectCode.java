package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.*;
import indi.wenyan.interpreter.structure.values.builtin.WenyanBuiltinObject;
import indi.wenyan.interpreter.structure.values.builtin.WenyanBuiltinObjectType;

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
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) throws WenyanThrowException {
        WenyanRuntime runtime = thread.currentRuntime();
        try {
            String id = runtime.bytecode.getIdentifier(args);
            switch (operation) {
                case ATTR, ATTR_REMAIN -> {
                    IWenyanValue attr;
                    IWenyanValue value = operation == Operation.ATTR ?
                            runtime.processStack.pop() : runtime.processStack.peek();
                    if (value.is(IWenyanObjectType.TYPE)) {
                        IWenyanObjectType object = value.as(IWenyanObjectType.TYPE);
                        attr = object.getAttribute(id);
                    } else {
                        IWenyanObject object = value.as(IWenyanObject.TYPE);
                        attr = object.getAttribute(id);
                    }
                    runtime.processStack.push(attr);
                }
                case STORE_STATIC_ATTR -> {
                    IWenyanValue value = WenyanLeftValue.varOf(runtime.processStack.pop());
                    WenyanBuiltinObjectType type = runtime.processStack.peek().as(WenyanBuiltinObjectType.TYPE);
                    type.addStaticVariable(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    IWenyanValue value = runtime.processStack.pop();
                    WenyanBuiltinObjectType type = runtime.processStack.peek().as(WenyanBuiltinObjectType.TYPE);
                    type.addFunction(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanBuiltinObject self = runtime.processStack.pop().as(WenyanBuiltinObject.TYPE);
                    IWenyanValue value = WenyanLeftValue.varOf(runtime.processStack.pop());
                    self.createAttribute(id, value);
                }
                case CREATE_TYPE -> {
                    var parent = runtime.processStack.pop();
                    IWenyanValue type;
                    if (parent.is(WenyanNull.TYPE))
                        type = new WenyanBuiltinObjectType(null);
                    else
                        type = new WenyanBuiltinObjectType(parent
                            .as(WenyanBuiltinObjectType.TYPE));
                    runtime.processStack.push(type);
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String name(Operation op) {
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
