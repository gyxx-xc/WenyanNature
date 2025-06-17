package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.utils.JavacallHandlers;

public class LocalCallHandler implements JavacallHandler {
    private final JavacallHandlers.WenyanFunction function;

    public LocalCallHandler(JavacallHandlers.WenyanFunction function) {
        this.function = function;
    }

    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return function.apply(context.args());
    }

    @Override
    public boolean isLocal() {
        return true;
    }

}
