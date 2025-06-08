package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;

public class LocalCallHandler implements JavacallHandler {
    private final WenyanFunction function;

    public LocalCallHandler(WenyanFunction function) {
        this.function = function;
    }

    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        return function.apply(args);
    }

    @Override
    public boolean isLocal() {
        return true;
    }

}
