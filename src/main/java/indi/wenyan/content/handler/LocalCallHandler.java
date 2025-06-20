package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

public class LocalCallHandler implements JavacallHandler {
    private final WenyanFunction function;

    public LocalCallHandler(WenyanFunction function) {
        this.function = function;
    }

    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return function.apply(context.args());
    }

    @Override
    public boolean isLocal(JavacallContext context) {
        return true;
    }

}
