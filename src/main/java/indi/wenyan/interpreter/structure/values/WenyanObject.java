package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import net.minecraft.network.chat.Component;

public interface WenyanObject extends WenyanValue {
    WenyanType<WenyanObject> TYPE = new WenyanType<>("object");

    /**
     * Get the attribute of this object.
     * <p>
     * Contains all variables and functions
     * in form of 'this' zhi ''name''.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    WenyanNativeValue getAttribute(String name);

    /**
     * Set a variable in this object.
     *
     * @param name  the name of the variable
     * @param value the value to set
     */
    void setVariable(String name, WenyanNativeValue value);

    default WenyanType<?> type() {
        return TYPE;
    }

    default  <T extends WenyanValue> T as(WenyanType<T> type) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_cast_").getString() +
                this.type() + Component.translatable("error.wenyan_nature._to_").getString() + type);
    }

    default void setValue(WenyanValue value) throws WenyanException.WenyanTypeException {
        throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.cannot_set_value_of_").getString() +
                this.type());
    }
}
