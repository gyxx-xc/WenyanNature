package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanDictObject implements WenyanObject {
    private final WenyanObjectType type;
    private final HashMap<String, WenyanNativeValue> variable = new HashMap<>();

    public WenyanDictObject(WenyanObjectType type) {
        this.type = type;
    }

    @Override
    public WenyanNativeValue getAttribute(String name) {
        var value = variable.get(name);
        if (value == null) {
            value = type.getFunction(name);
        }
        return value;
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        variable.put(name, value);
    }

    @Override
    public WenyanObjectType getParent() {
        return type.getParent();
    }
}
