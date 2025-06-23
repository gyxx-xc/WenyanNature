package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface WenyanObjectType extends WenyanFunction {
    WenyanType<WenyanObjectType> TYPE = new WenyanType<>("object_type");

    WenyanValue getAttribute(String name);

    WenyanObject createObject(List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    default void call(WenyanValue self, WenyanThread thread, List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(createObject(argsList));
    }

    default WenyanType<?> type() {
        return TYPE;
    }

    default  <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        if (type() == type) {
            return (T) this;
        }
        if (type == WenyanFunction.TYPE) {
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
