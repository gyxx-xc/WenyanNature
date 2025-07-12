package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public class LocalCallHandler implements IJavacallHandler {
    private final LocalFunction function;

    public LocalCallHandler(LocalFunction function) {
        this.function = function;
    }

    public IWenyanValue handle(IWenyanValue self, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        return function.apply(self, argsList);
    }

    @Override
    public void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(handle(self, argsList));
    }

    @FunctionalInterface
    public
    interface LocalFunction {
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args) throws WenyanException.WenyanThrowException;
    }
}
