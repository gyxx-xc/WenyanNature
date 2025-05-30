package indi.wenyan.interpreter.structure;

import java.util.HashMap;

public class WenyanObject {
    public final WenyanObjectType type;
    public final HashMap<String, WenyanValue> variable;

    public WenyanObject(WenyanObjectType type, HashMap<String, WenyanValue> variable) {
        this.type = type;
        this.variable = variable;
    }
}
