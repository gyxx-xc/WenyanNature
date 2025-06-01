package indi.wenyan.interpreter.executor;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.WenyanCode;
import indi.wenyan.interpreter.utils.WenyanProgram;
import net.minecraft.network.chat.Component;

public class ObjectCode extends WenyanCode {
    private final Operation operation;

    public ObjectCode(Operation operation) {
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanProgram program) {
        WenyanRuntime runtime = program.runtimes.peek();
        try {
            String id = runtime.bytecode.getIdentifier(args);
            switch (operation) {
                case ATTR -> {
                    WenyanValue attr;
                    WenyanValue value = runtime.processStack.pop();
                    if (value.getType() == WenyanValue.Type.OBJECT_TYPE) { // static
                        WenyanObjectType type = (WenyanObjectType) value
                                .casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                        attr = type.staticVariable.get(id);
                        if (attr == null) {
                            attr = type.getFunction(id);
                        }
                    } else { // object or other(casting to object)
                        WenyanObject object = (WenyanObject) value
                                .casting(WenyanValue.Type.OBJECT).getValue();
                        attr = object.variable.get(id);
                        if (attr == null) {
                            attr = object.type.getFunction(id);
                        }
                    }
                    runtime.processStack.push(attr);
                }
                case ATTR_REMAIN -> {
                    WenyanValue attr;
                    WenyanValue value = runtime.processStack.peek();
                    // copy from ATTR
                    if (value.getType() == WenyanValue.Type.OBJECT_TYPE) { // static
                        WenyanObjectType type = (WenyanObjectType) value
                                .casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                        attr = type.staticVariable.get(id);
                        if (attr == null) {
                            attr = type.getFunction(id);
                        }
                    } else { // object or other(casting to object)
                        WenyanObject object = (WenyanObject) value
                                .casting(WenyanValue.Type.OBJECT).getValue();
                        attr = object.variable.get(id);
                        if (attr == null) {
                            attr = object.type.getFunction(id);
                        }
                    }
                    runtime.processStack.push(attr);
                }
                case STORE_STATIC_ATTR -> {
                    WenyanValue value = WenyanValue.varOf(runtime.processStack.pop());
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek()
                            .casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                    type.staticVariable.put(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    WenyanValue value = runtime.processStack.pop();
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek()
                            .casting(WenyanValue.Type.OBJECT_TYPE).getValue();
                    type.functions.put(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanObject self = (WenyanObject) runtime.processStack.pop()
                            .casting(WenyanValue.Type.OBJECT).getValue();
                    WenyanValue value = WenyanValue.varOf(runtime.processStack.pop());
                    self.variable.put(id, value);
                }
                case CREATE_TYPE -> {
                    WenyanValue type = new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                            new WenyanObjectType((WenyanObjectType) runtime.processStack.pop()
                                    .casting(WenyanValue.Type.OBJECT_TYPE).getValue(), id), true);
                    runtime.processStack.push(type);
                }
                case CREATE_OBJECT -> {
                    // create empty, run constructor, return self
                    WenyanValue instance = new WenyanValue(WenyanValue.Type.OBJECT,
                            new WenyanObject((WenyanObjectType) runtime.processStack.pop()
                                    .casting(WenyanValue.Type.OBJECT_TYPE).getValue()), false);
                    // copy from functionCode's call
                    WenyanValue[] argsList = new WenyanValue[args];
                    WenyanValue.FunctionSign sign = (WenyanValue.FunctionSign)
                            ((WenyanObject) instance.getValue())
                            .type.getFunction("造").getValue();
                    if (sign.argTypes().length != args)
                        throw new WenyanException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                    for (int i = 0; i < args; i++) // although here's a O(n), the function is already paid for it
                        argsList[i] = runtime.processStack.pop().casting(sign.argTypes()[i]);
                    WenyanRuntime constructorRuntime = new WenyanRuntime((WenyanBytecode) sign.bytecode());
                    constructorRuntime.setVariable("己", instance);
                    constructorRuntime.setVariable("父", new WenyanValue(WenyanValue.Type.OBJECT_TYPE,
                                    ((WenyanObject) instance.getValue()).type.parent, true));
                    for (int i = 0; i < args; i ++)
                        constructorRuntime.setVariable(
                                ((WenyanBytecode) sign.bytecode()).getIdentifier(i),
                                WenyanValue.varOf(argsList[i]));
                    constructorRuntime.noReturnFlag = true;
                    program.runtimes.push(constructorRuntime);
                    runtime.processStack.push(instance);
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
