package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanObjectType {
    public final WenyanObjectType parent;
    public final String name;
    public final HashMap<String, WenyanValue> staticVariable = new HashMap<>();
    public final HashMap<String, WenyanValue> functions = new HashMap<>();

    public WenyanObjectType(WenyanObjectType parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public WenyanValue getFunction(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunction(id);
        } else {
            throw new WenyanException("Function not found: " + id);
        }
    }
}
