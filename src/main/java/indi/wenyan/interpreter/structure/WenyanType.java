package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

public enum WenyanType {
    NULL,
    INT, // used as default number type
    DOUBLE,
    BOOL,
    STRING,
    LIST,
    OBJECT,
    OBJECT_TYPE,
    FUNCTION;

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_nature." + name().toLowerCase()).getString();
    }
}
