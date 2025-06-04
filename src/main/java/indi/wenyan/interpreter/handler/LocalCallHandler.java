package indi.wenyan.interpreter.handler;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandler;

public class LocalCallHandler extends JavacallHandler {
    private final WenyanFunction function;

    public LocalCallHandler(WenyanFunction function) {
        this.function = function;
    }

    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return function.apply(args);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
