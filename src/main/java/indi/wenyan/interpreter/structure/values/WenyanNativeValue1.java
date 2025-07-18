package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;

// Deprecated and change to WenyanValue
@Deprecated
@SuppressWarnings("unused")
public class WenyanNativeValue1 implements IWenyanValue {

    private final WenyanType<?> type;
    @Setter
    @Getter
    private Object value;

    public WenyanNativeValue1(WenyanType<?> type, Object value, boolean isConst) {
        this.type = type;
        this.value = value;
    }

    public WenyanType<?> type() {
        return type;
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
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_programming.cannot_cast_").getString() + this.type + Component.translatable("error.wenyan_programming._to_").getString() + type);
    }
}
