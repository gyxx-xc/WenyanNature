package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.*;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

// about to Deprecated and change to WenyanValue
@Deprecated
public class WenyanNativeValue implements WenyanValue {

    private final WenyanType<?> type;
    private Object value;
    private final boolean isConst;

    public WenyanNativeValue(WenyanType<?> type, Object value, boolean isConst) {
        this.type = type;
        this.value = value;
        this.isConst = isConst;
    }

    public WenyanType<?> type() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isConst() {
        return isConst;
    }

    public static WenyanNativeValue varOf(WenyanNativeValue value) {
        Object value1 = value.value;
        // STUB: I don't think this will be remained
        if (value.value instanceof WenyanArrayObject arrayObject) {
            value1 = new WenyanArrayObject(new ArrayList<>(arrayObject.values));
        }
        return new WenyanNativeValue(value.type, value1, false);
    }

    // what we need to do these function?
    // 1. wide link
    // 2. required type

    // 1. wide link
    // string <- big_int, double <- int <- bool
    //      ^- list

    // 2. required type
    // downgrade + wide link
    // double -> int
    // ~list -> bool
    // obj -> function (constructor)
    public <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    @Override
    public void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {

    }
}
