package indi.wenyan.interpreter.structure.values;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;

import java.util.List;

public interface WenyanObjectType extends WenyanFunction {
    WenyanType<WenyanObjectType> TYPE = new WenyanType<>("object_type", WenyanObjectType.class);

    WenyanValue getAttribute(String name);

    WenyanObject createObject(List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    default void call(WenyanValue self, WenyanThread thread, List<WenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(createObject(argsList));
    }
}
