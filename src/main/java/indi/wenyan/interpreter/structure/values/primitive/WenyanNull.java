package indi.wenyan.interpreter.structure.values.primitive;

import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.network.chat.Component;

public final class WenyanNull implements IWenyanValue {
    public static final IWenyanValue NULL = new WenyanNull();
    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>("null", WenyanNull.class);

    private WenyanNull(){}

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_programming.null").getString();
    }
}
