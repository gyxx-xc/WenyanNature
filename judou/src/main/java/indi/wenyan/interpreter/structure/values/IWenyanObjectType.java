package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.WenyanThreading;

import java.util.List;

/**
 * Interface representing a Wenyan object type that can create new instances.
 */
public interface IWenyanObjectType extends IWenyanFunction {
    WenyanType<IWenyanObjectType> TYPE = new WenyanType<>("object_type", IWenyanObjectType.class);

    /**
     * Gets an attribute from this object type.
     *
     * @param name the name of the attribute to get
     * @return the value of the attribute
     */
    IWenyanValue getAttribute(String name) throws WenyanThrowException;

    /**
     * Creates a new object instance of this type.
     *
     * @param argsList the arguments to pass to the constructor
     * @return the new object instance
     * @throws WenyanThrowException if object creation fails
     */
    @WenyanThreading
    IWenyanObject createObject(List<IWenyanValue> argsList)
            throws WenyanThrowException;

    @Override
    default void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList)
            throws WenyanThrowException {
        thread.currentRuntime().pushReturnValue(createObject(argsList));
    }
}
