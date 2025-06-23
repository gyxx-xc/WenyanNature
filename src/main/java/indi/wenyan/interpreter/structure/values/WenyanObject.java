package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

public interface WenyanObject extends WenyanValue {
    WenyanType<WenyanObject> TYPE = new WenyanType<>("object", WenyanObject.class);

    /**
     * Get the attribute of this object.
     * <p>
     * Contains all variables and functions
     * in form of 'this' zhi ''name''.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    WenyanValue getAttribute(String name);

    /**
     * Set a variable in this object.
     *
     * @param name  the name of the variable
     * @param value the value to set
     */
    void setVariable(String name, WenyanValue value);
}
