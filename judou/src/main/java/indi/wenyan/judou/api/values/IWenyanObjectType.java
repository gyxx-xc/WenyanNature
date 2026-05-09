package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

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

    /**
     * Creates a new object instance of this type.
     *
     * @param argsList the arguments to pass to the constructor
     * @return the new object instance
     * @throws WenyanException if object creation fails
     */
    IWenyanObject createObject(List<IWenyanValue> argsList)
            throws WenyanException;

    @Override
    default void call(IWenyanValue self, @UnknownNullability IWenyanRunner thread, List<IWenyanValue> argsList)
            throws WenyanException {
        thread.getCurrentRuntime().pushReturnValue(createObject(argsList));
    }
}
