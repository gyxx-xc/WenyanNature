package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

import java.util.List;

public class LocalCallHandler implements JavacallHandler {
    private final LocalFunction function;

    public LocalCallHandler(LocalFunction function) {
        this.function = function;
    }

    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return function.apply(context.self(), context.args());
    }

    @Override
    public boolean isLocal(JavacallContext context) {
        return true;
    }

    @FunctionalInterface
    public
    interface LocalFunction {
        WenyanNativeValue apply(WenyanNativeValue self, List<WenyanNativeValue> args) throws WenyanException.WenyanThrowException;
    }
}
