package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanDictObject implements WenyanObject {
    private final WenyanObjectType type;
    private final HashMap<String, WenyanValue> variable = new HashMap<>();

    public WenyanDictObject(WenyanObjectType type) {
        this.type = type;
    }

    @Override
    public WenyanValue getVariable(String name) {
        return variable.getOrDefault(name, WenyanValue.NULL);
    }

    @Override
    public void setVariable(String name, WenyanValue value) {
        variable.put(name, value);
    }

    @Override
    public WenyanValue getFunction(String name) {
        return type.getFunction(name);
    }

    @Override
    public WenyanObjectType getType() {
        return type;
    }
}
