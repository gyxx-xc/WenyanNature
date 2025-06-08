package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanDictObject implements WenyanObject {
    private final WenyanObjectType type;
    private final HashMap<String, WenyanNativeValue> variable = new HashMap<>();

    public WenyanDictObject(WenyanObjectType type) {
        this.type = type;
    }

    @Override
    public WenyanNativeValue getVariable(String name) {
        return variable.getOrDefault(name, WenyanNativeValue.NULL);
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        variable.put(name, value);
    }

    @Override
    public WenyanNativeValue getFunction(String name) {
        return type.getFunction(name);
    }

    @Override
    public WenyanObjectType getType() {
        return type;
    }
}
