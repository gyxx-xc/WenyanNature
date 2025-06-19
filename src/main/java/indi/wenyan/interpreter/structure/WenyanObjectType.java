package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public interface WenyanObjectType extends WenyanValue {
    default WenyanNativeValue getAttribute(String name) {
        var attr = getStaticVariable(name);
        if (attr == null) attr = getFunction(name);
        if (attr == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.function_not_found_").getString() + name);
        else
            return attr;
    }

    @Nullable
    WenyanObjectType getParent();

    WenyanNativeValue getFunction(String id);

    WenyanNativeValue getStaticVariable(String id);

    default WenyanType type() {
        return WenyanType.OBJECT_TYPE;
    }
}
