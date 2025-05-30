package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanObjectType {
    public final WenyanObjectType parent;
    public final String name;
    public final HashMap<String, WenyanValue> staticVariable = new HashMap<>();

    public WenyanObjectType(WenyanObjectType parent, String name) {
        this.parent = parent;
        this.name = name;
    }
}
