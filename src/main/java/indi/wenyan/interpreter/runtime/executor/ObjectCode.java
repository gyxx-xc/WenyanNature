package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.values.*;

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
                    WenyanValue attr;
                    WenyanValue value = operation == Operation.ATTR ?
                            runtime.processStack.pop() : runtime.processStack.peek();
                    if (value.is(WenyanObjectType.TYPE)) {
                        WenyanObjectType object = value.as(WenyanObjectType.TYPE);
                        attr = object.getAttribute(id);
                    } else {
                        WenyanObject object = value.as(WenyanObject.TYPE);
                        attr = object.getAttribute(id);
                    }
                    runtime.processStack.push(attr);
                }
                case STORE_STATIC_ATTR -> {
                    WenyanValue value = WenyanLeftValue.varOf(runtime.processStack.pop());
                    WenyanDictObjectType type = runtime.processStack.peek().as(WenyanDictObjectType.TYPE);
                    type.addStaticVariable(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanDictObjectType type = runtime.processStack.peek().as(WenyanDictObjectType.TYPE);
                    type.addFunction(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanObject self = runtime.processStack.pop().as(WenyanObject.TYPE);
                    WenyanValue value = WenyanLeftValue.varOf(runtime.processStack.pop());
                    self.setVariable(id, value);
                }
                case CREATE_TYPE -> {
                    var parent = runtime.processStack.pop();
                    WenyanValue type;
                    if (parent.is(WenyanNull.TYPE))
                        type = new WenyanDictObjectType(null);
                    else
                        type = new WenyanDictObjectType(parent
                            .as(WenyanDictObjectType.TYPE));
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
