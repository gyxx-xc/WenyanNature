package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

public interface WenyanObjectType {
    default WenyanNativeValue getAttribute(String name) {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunction(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_").getString() + name);
        else
            return attr;
    }

    String getName();

    WenyanObjectType getParent();

    WenyanNativeValue getFunction(String id);

    void addFunction(String id, WenyanNativeValue function);

    WenyanNativeValue getStaticVariable(String id);

    void addStaticVariable(String id, WenyanNativeValue value);

    default WenyanType type() {
        return WenyanType.OBJECT_TYPE;
    }
}
