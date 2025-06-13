package indi.wenyan.interpreter.structure;

import javax.annotation.Nullable;

public interface WenyanObject extends WenyanValue {
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

    /**
     * Get the parent type of this object.
     * <p>
     * Just returns null if you are not sure
     *
     * @return the parent WenyanObjectType
     */
    @Nullable
    WenyanObjectType getParent();

    default WenyanType type() {
        return WenyanType.OBJECT;
    }
}
