package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public enum WenyanNull implements IWenyanValue {
    NULL;

    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>("null", WenyanNull.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_programming.null").getString();
    }
}
