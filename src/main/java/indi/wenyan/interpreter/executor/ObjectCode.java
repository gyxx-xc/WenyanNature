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
            String id = runtime.bytecode.getIdentifier(args);
            switch (operation) {
                case ATTR -> {
                    WenyanValue attr = null;
                    WenyanValue value = runtime.processStack.pop();
                    if (value.getType() == WenyanValue.Type.OBJECT) {
                        WenyanObject object = (WenyanObject) value.casting(WenyanValue.Type.OBJECT).getValue();
                        attr = object.variable.get(id);
                        if (attr == null) {
                            attr = object.type.getFunction(id);
                        }
                    }
                    if (value.getType() == WenyanValue.Type.OBJECT_TYPE) { // static
                        WenyanObjectType type = (WenyanObjectType) value.casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                        attr = type.staticVariable.get(id);
                        if (attr == null) {
                            attr = type.getFunction(id);
                        }
                    }
                    runtime.processStack.push(attr);
                }
                case ATTR_REMAIN -> {
                    // this bytecode should only be used with on instance that call function, so no static
                    WenyanObject object = (WenyanObject) runtime.processStack.peek().casting(WenyanValue.Type.OBJECT).getValue();
                    runtime.processStack.push(object.type.getFunction(id));
                }
                case STORE_STATIC_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek().casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                    type.staticVariable.put(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek().casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                    type.functions.put(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanObject self = (WenyanObject) runtime.processStack.pop().casting(WenyanValue.Type.OBJECT).getValue();
                    WenyanValue value = runtime.processStack.pop();
                    self.variable.put(id, value);
                }
                case CREATE_TYPE -> {
                    WenyanValue type = new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                            new WenyanObjectType(null, id), true);
                    runtime.processStack.push(type);
                }
                case CREATE_OBJECT -> {
                    // create empty, run constructor, return self
                    WenyanValue instance = new WenyanValue(WenyanValue.Type.OBJECT,
                            new WenyanObject((WenyanObjectType) runtime.processStack.pop().casting(WenyanValue.Type.OBJECT_TYPE).getValue()), false);

                    // copy from function call
                    WenyanValue[] argsList = new WenyanValue[args];
                    WenyanValue.FunctionSign sign = (WenyanValue.FunctionSign)
                            ((WenyanObject) instance.getValue())
                            .type.getFunction("造").getValue();
                    for (int i = 0; i < args; i++)
                        argsList[i] = runtime.processStack.pop();
                    runtime.nextRuntime = new WenyanRuntime(runtime, (WenyanBytecode) sign.bytecode());
                    runtime.nextRuntime.setVariable("己", instance);
                    for (int i = 1; i < args; i ++)
                        runtime.nextRuntime.setVariable(
                                ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                                WenyanValue.varOf(argsList[i]));
                    runtime.nextRuntime.noReturnFlag = true;
                    runtime.changeRuntimeFlag = true;
                    runtime.processStack.push(instance);
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
            case STORE_STATIC_ATTR -> "STORE_STATIC_ATTR";
            case STORE_FUNCTION_ATTR -> "STORE_FUNCTION_ATTR";
            case STORE_ATTR -> "STORE_ATTR";
            case CREATE_TYPE -> "CREATE_TYPE";
            case CREATE_OBJECT -> "CREATE_OBJECT";
        };
    }

    public enum Operation {
        ATTR,
        ATTR_REMAIN,
        STORE_ATTR,
        STORE_STATIC_ATTR,
        STORE_FUNCTION_ATTR,
        CREATE_TYPE,
        CREATE_OBJECT
    }
}
