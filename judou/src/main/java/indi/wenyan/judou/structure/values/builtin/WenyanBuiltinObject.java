package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.HashMap;
import java.util.Map;

/**
 * A built-in object implementation for Wenyan.
 * Represents an object created in Wenyan code.
 */
public class WenyanBuiltinObject implements IWenyanObject {
    private final WenyanBuiltinObjectType type;
    private final Map<String, IWenyanValue> variable = new HashMap<>();
    public static final WenyanType<WenyanBuiltinObject> TYPE = new WenyanType<>("dict_object", WenyanBuiltinObject.class);

    /**
     * Creates a new built-in object of the specified type.
     *
     * @param type the type of the object
     */
    public WenyanBuiltinObject(WenyanBuiltinObjectType type) {
        this.type = type;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanThrowException {
        var value = variable.get(name);
        if (value == null) {
            value = type.getFunction(name);
        }
        return value;
    }

    public void createAttribute(String name, IWenyanValue value) {
        variable.put(name, value);
    }

    /**
     * Gets the object type of this object.
     *
     * @return the object type
     */
    public WenyanBuiltinObjectType getObjectType() {
        return type;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
