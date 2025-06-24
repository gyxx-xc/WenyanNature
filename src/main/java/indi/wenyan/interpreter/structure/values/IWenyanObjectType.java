package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

import java.util.List;

public interface IWenyanObjectType extends IWenyanFunction {
    WenyanType<IWenyanObjectType> TYPE = new WenyanType<>("object_type", IWenyanObjectType.class);

    IWenyanValue getAttribute(String name);

    IWenyanObject createObject(List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    default void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(createObject(argsList));
    }
}
