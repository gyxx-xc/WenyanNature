package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class WenyanDictObjectType implements WenyanObjectType {
    private final WenyanObjectType parent;
    private final String name;
    private final HashMap<String, WenyanNativeValue> staticVariable = new HashMap<>();
    private final HashMap<String, WenyanNativeValue> functions = new HashMap<>();

    public WenyanDictObjectType(WenyanObjectType parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WenyanObjectType getParent() {
        return parent;
    }

    @Override
    public WenyanNativeValue getFunction(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunction(id);
        } else {
            return null;
        }
    }

    @Override
    public void addFunction(String id, WenyanNativeValue function) {
        functions.put(id, function);
    }

    @Override
    public WenyanNativeValue getStaticVariable(String id) {
        return staticVariable.get(id);
    }

    @Override
    public void addStaticVariable(String id, WenyanNativeValue value) {
        staticVariable.put(id, value);
    }
}
