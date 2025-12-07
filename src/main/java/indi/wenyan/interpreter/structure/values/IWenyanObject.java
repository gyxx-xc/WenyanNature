package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.structure.WenyanType;

/**
 * Interface representing an object in Wenyan language.
 * Objects have attributes that can be accessed and modified.
 */
public interface IWenyanObject extends IWenyanValue {
    WenyanType<IWenyanObject> TYPE = new WenyanType<>("object", IWenyanObject.class);

    /**
     * Get the attribute of this object.
     * <p>
     * Contains all variables and functions
     * in form of 'this' zhi ''name''.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    IWenyanValue getAttribute(String name);

    /**
     * Set a variable in this object.
     *
     * @param name  the name of the variable
     * @param value the value to set
     */
    void setAttribute(String name, IWenyanValue value);
}
