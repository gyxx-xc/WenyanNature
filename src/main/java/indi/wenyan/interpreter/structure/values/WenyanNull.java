package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public final class WenyanNull implements WenyanValue {
    public static final WenyanValue NULL = new WenyanNull();
    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>("null", WenyanNull.class);

    private WenyanNull(){}

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_nature.null").getString();
    }
}
