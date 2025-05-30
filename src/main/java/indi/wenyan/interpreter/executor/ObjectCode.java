package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.WenyanCode;

public class ObjectCode extends WenyanCode {
    private final Operation operation;

    public ObjectCode(Operation operation) {
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanRuntime runtime) {
        try {
            switch (operation) {
                case ATTR -> {
                    WenyanObject object = (WenyanObject) runtime.processStack.pop().casting(WenyanValue.Type.OBJECT).getValue();
                    runtime.processStack.push(object.variable.get(runtime.bytecode.getIdentifier(args)));
                }
                case ATTR_REMAIN -> {
                    WenyanObject object = (WenyanObject) runtime.processStack.peek().casting(WenyanValue.Type.OBJECT).getValue();
                    runtime.processStack.push(object.variable.get(runtime.bytecode.getIdentifier(args)));
                }
                case STORE_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek().casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                    type.staticVariable.put(runtime.bytecode.getIdentifier(args), value);
                }
                case CREATE_TYPE -> {
                    WenyanValue type = WenyanValue.emptyOf(WenyanValue.Type.OBJECT_TYPE, false);
                    runtime.processStack.push(type);
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            throw new RuntimeException(e);
        }
    }

    private static String name(Operation op) {
        return switch (op) {
            case ATTR -> "LOAD_ATTR";
            case ATTR_REMAIN -> "LOAD_ATTR_REMAIN";
            case STORE_ATTR -> "STORE_ATTR";
            case CREATE_TYPE -> "CREATE_TYPE";
        };
    }

    public enum Operation {
        ATTR,
        ATTR_REMAIN,
        STORE_ATTR,
        CREATE_TYPE,
    }
}
