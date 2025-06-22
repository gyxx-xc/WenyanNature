package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * since a function can be a bytecode or a native function,
 * we define an empty interface to represent a function in Wenyan
 */
public interface WenyanFunction extends WenyanValue {
    WenyanType<WenyanFunction> TYPE = new WenyanType<>("function");

    void call(WenyanValue self, WenyanThread thread,
              List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    default WenyanType<?> type() { return TYPE; }

    default  <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type() == type) {
            return (T) this;
        }
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    default void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_set_value_of_").getString() +
                this.type());
    }
}
