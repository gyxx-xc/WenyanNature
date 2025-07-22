package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public class WenyanBuiltinFunction implements IJavacallHandler {
    private final BuiltinFunction function;

    public WenyanBuiltinFunction(BuiltinFunction function) {
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
    public interface BuiltinFunction {
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args)
                throws WenyanException.WenyanThrowException;
    }
}
