package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public class WenyanNull implements WenyanValue {
    public static final WenyanValue NULL = new WenyanNull();
    public static final WenyanType<WenyanNull> TYPE = new WenyanType<>("null");

    private WenyanNull(){}

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_set_value_to_null").getString());
    }

    @Override
    public String toString() {
        return Component.translatable("type.wenyan_nature.null").getString();
    }
}
