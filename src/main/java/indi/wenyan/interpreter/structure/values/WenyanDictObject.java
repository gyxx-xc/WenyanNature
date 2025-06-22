package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

import java.util.HashMap;

public class WenyanDictObject implements WenyanObject {
    private final WenyanDictObjectType type;
    private final HashMap<String, WenyanValue> variable = new HashMap<>();
    public static final WenyanType<WenyanDictObject> TYPE = new WenyanType<>("dict_object");

    public WenyanDictObject(WenyanDictObjectType type) {
        this.type = type;
    }

    @Override
    public WenyanValue getAttribute(String name) {
        var value = variable.get(name);
        if (value == null) {
            value = type.getFunction(name);
        }
        return value;
    }

    @Override
    public void setVariable(String name, WenyanValue value) {
        variable.put(name, value);
    }

    public WenyanDictObjectType getObjectType() {
        return type;
    }
}
