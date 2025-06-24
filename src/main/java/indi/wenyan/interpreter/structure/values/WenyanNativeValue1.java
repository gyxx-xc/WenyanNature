package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

// about to Deprecated and change to WenyanValue
@Deprecated
public class WenyanNativeValue1 implements IWenyanValue {

    private final WenyanType<?> type;
    private Object value;
    private final boolean isConst;

    public WenyanNativeValue1(WenyanType<?> type, Object value, boolean isConst) {
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
    public <T extends IWenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }
}
