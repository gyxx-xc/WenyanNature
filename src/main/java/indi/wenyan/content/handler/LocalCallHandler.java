package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public class LocalCallHandler implements IJavacallHandler {
    private final LocalFunction function;

    public LocalCallHandler(LocalFunction function) {
        this.function = function;
    }

    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return function.apply(context.self(), context.args());
    }

    @Override
    public boolean isLocal(JavacallContext context) {
        return true;
    }

    @FunctionalInterface
    public
    interface LocalFunction {
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args) throws WenyanException.WenyanThrowException;
    }
}
