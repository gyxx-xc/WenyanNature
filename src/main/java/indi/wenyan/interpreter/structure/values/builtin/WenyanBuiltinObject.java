package indi.wenyan.interpreter.structure.values.builtin;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.HashMap;
import java.util.Map;

public class WenyanBuiltinObject implements IWenyanObject {
    private final WenyanBuiltinObjectType type;
    private final Map<String, IWenyanValue> variable = new HashMap<>();
    public static final WenyanType<WenyanBuiltinObject> TYPE = new WenyanType<>("dict_object", WenyanBuiltinObject.class);

    public WenyanBuiltinObject(WenyanBuiltinObjectType type) {
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

    public WenyanBuiltinObjectType getObjectType() {
        return type;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
