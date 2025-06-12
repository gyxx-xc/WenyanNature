package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

public interface WenyanObject extends WenyanValue {
    default WenyanNativeValue getAttribute(String name) {
        var attr = getVariable(name);
        if (attr == null) attr = getFunction(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_").getString() + name);
        return attr;
    }

    WenyanNativeValue getVariable(String name);

    void setVariable(String name, WenyanNativeValue value);

    WenyanNativeValue getFunction(String name);

    WenyanObjectType getType();

    default WenyanType type() {
        return WenyanType.OBJECT;
    }
}
