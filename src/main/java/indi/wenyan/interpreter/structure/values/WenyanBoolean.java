package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public class WenyanBoolean implements WenyanValue {
    public static final WenyanType<WenyanBoolean> TYPE = new WenyanType<>("bool");
    public Boolean value;

    public WenyanBoolean(Boolean value) {
        this.value = value;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (this.type() == type)
            return (T) this;
        if (type == WenyanString.TYPE) {
            return (T) new WenyanString(toString());
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    public WenyanBoolean not() throws WenyanException.WenyanTypeException {
        return new WenyanBoolean(!value);
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        this.value = value.as(TYPE).value;
    }

    @Override
    public String toString() {
        return value ? "陽" : "陰";
    }
}
