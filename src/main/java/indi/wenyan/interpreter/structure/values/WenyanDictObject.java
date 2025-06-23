package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

import java.util.HashMap;
import java.util.Map;

public class WenyanDictObject implements WenyanObject {
    private final WenyanDictObjectType type;
    private final Map<String, WenyanValue> variable = new HashMap<>();
    public static final WenyanType<WenyanDictObject> TYPE = new WenyanType<>("dict_object", WenyanDictObject.class);

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

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
