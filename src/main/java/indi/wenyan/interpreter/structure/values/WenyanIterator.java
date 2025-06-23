package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import java.util.Iterator;

public class WenyanIterator implements WenyanValue {
    public static final WenyanType<WenyanIterator> TYPE = new WenyanType<>("iterator");
    public Iterator<WenyanValue> value;

    public WenyanIterator(Iterator<WenyanValue> value) {
        this.value = value;
    }

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

    }
}
