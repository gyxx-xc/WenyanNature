package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.values.exception.WenyanException;

/**
 * Interface representing a Wenyan object type that can create new instances.
 */
public interface IWenyanObjectType extends IWenyanFunction {
    WenyanType<IWenyanObjectType> TYPE = new WenyanType<>(JudouTypeText.ObjectType.string(), IWenyanObjectType.class);

    /**
     * Gets an attribute from this object type.
     *
     * @param name the name of the attribute to get
     * @return the value of the attribute
     */
    IWenyanValue getAttribute(String name) throws WenyanException;
}
