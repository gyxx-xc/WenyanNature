package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanLeftValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.wynative.WenyanNativeObjectType;

public class ObjectCode extends WenyanCode {
    private final Operation operation;

    public ObjectCode(Operation operation) {
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
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
                    WenyanNativeObjectType type = runtime.processStack.peek().as(WenyanNativeObjectType.TYPE);
                    type.addStaticVariable(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    IWenyanValue value = runtime.processStack.pop();
                    WenyanNativeObjectType type = runtime.processStack.peek().as(WenyanNativeObjectType.TYPE);
                    type.addFunction(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    IWenyanObject self = runtime.processStack.pop().as(IWenyanObject.TYPE);
                    IWenyanValue value = WenyanLeftValue.varOf(runtime.processStack.pop());
                    self.setVariable(id, value);
                }
                case CREATE_TYPE -> {
                    var parent = runtime.processStack.pop();
                    IWenyanValue type;
                    if (parent.is(WenyanNull.TYPE))
                        type = new WenyanNativeObjectType(null);
                    else
                        type = new WenyanNativeObjectType(parent
                            .as(WenyanNativeObjectType.TYPE));
                    runtime.processStack.push(type);
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
    }

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

    public enum Operation {
        ATTR,
        ATTR_REMAIN,
        STORE_ATTR,
        STORE_STATIC_ATTR,
        STORE_FUNCTION_ATTR,
        CREATE_TYPE
    }
}
