package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class WenyanDictObjectType implements WenyanObjectType {
    private final WenyanObjectType parent;
    private final String name;
    private final HashMap<String, WenyanValue> staticVariable = new HashMap<>();
    private final HashMap<String, WenyanValue> functions = new HashMap<>();

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
    public WenyanValue getFunction(String id) {
        if (functions.containsKey(id)) {
            return functions.get(id);
        } else if (parent != null) {
            return parent.getFunction(id);
        } else {
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_") + id);
        }
    }

    @Override
    public void addFunction(String id, WenyanValue function) {
        if (functions.containsKey(id)) {
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_already_exists_") + id);
        }
        functions.put(id, function);
    }

    @Override
    public WenyanValue getStaticVariable(String id) {
        if (staticVariable.containsKey(id)) {
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_") + id);
        }
        return staticVariable.get(id);
    }

    @Override
    public void addStaticVariable(String id, WenyanValue value) {
        if (staticVariable.containsKey(id)) {
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_already_exists_") + id);
        }
        staticVariable.put(id, value);
    }
}
