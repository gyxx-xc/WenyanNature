package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.runtime.WenyanThread;

import java.util.List;

public interface WenyanObjectType extends WenyanValue, WenyanFunction {
    WenyanNativeValue getAttribute(String name);

    WenyanObject createObject(List<WenyanNativeValue> argsList)
            throws WenyanException.WenyanThrowException;

    @Override
    default void call(WenyanNativeValue self, WenyanThread thread, List<WenyanNativeValue> argsList)
            throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(new WenyanNativeValue(WenyanType.OBJECT,
                createObject(argsList), true));
    }

    default WenyanType type() {
        return WenyanType.OBJECT_TYPE;
    }
}
