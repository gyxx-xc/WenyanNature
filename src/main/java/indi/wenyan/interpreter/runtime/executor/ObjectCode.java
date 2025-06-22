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
                    WenyanNativeValue1 value = operation == Operation.ATTR ?
                            runtime.processStack.pop() : runtime.processStack.peek();
                    if (value.type() == WenyanObjectType.TYPE) {
                        WenyanObjectType object = (WenyanObjectType) value
                                .casting(WenyanObjectType.TYPE).getValue();
                        attr = object.getAttribute(id);
                    } else {
                        WenyanObject object = (WenyanObject) value
                                .casting(WenyanObject.TYPE).getValue();
                        attr = object.getAttribute(id);
                    }
                    runtime.processStack.push(attr);
                }
                case STORE_STATIC_ATTR -> {
                    WenyanValue value = WenyanNativeValue1.varOf(runtime.processStack.pop());
                    WenyanDictObjectType type = (WenyanDictObjectType) runtime.processStack.peek()
                            .casting(WenyanObjectType.TYPE).getValue();
                    type.addStaticVariable(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanDictObjectType type = (WenyanDictObjectType) runtime.processStack.peek()
                            .casting(WenyanObjectType.TYPE).getValue();
                    type.addFunction(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanObject self = (WenyanObject) runtime.processStack.pop()
                            .casting(WenyanObject.TYPE).getValue();
                    WenyanValue value = WenyanNativeValue1.varOf(runtime.processStack.pop());
                    self.setVariable(id, value);
                }
                case CREATE_TYPE -> {
                    WenyanValue type = new WenyanNativeValue1(WenyanObjectType.TYPE,
                            new WenyanDictObjectType((WenyanDictObjectType) runtime.processStack.pop()
                                    .casting(WenyanObjectType.TYPE).getValue()), true);
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
