package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanObject {
    public final WenyanObjectType type;
    public final HashMap<String, WenyanValue> variable = new HashMap<>();

    public WenyanObject(WenyanObjectType type) {
        this.type = type;
    }
}
