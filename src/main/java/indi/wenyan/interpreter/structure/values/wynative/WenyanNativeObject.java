package indi.wenyan.interpreter.structure.values.wynative;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.HashMap;
import java.util.Map;

public class WenyanNativeObject implements IWenyanObject {
    private final WenyanNativeObjectType type;
    private final Map<String, IWenyanValue> variable = new HashMap<>();
    public static final WenyanType<WenyanNativeObject> TYPE = new WenyanType<>("dict_object", WenyanNativeObject.class);

    public WenyanNativeObject(WenyanNativeObjectType type) {
        this.type = type;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        var value = variable.get(name);
        if (value == null) {
            value = type.getFunction(name);
        }
        return value;
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        variable.put(name, value);
    }

    public WenyanNativeObjectType getObjectType() {
        return type;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
