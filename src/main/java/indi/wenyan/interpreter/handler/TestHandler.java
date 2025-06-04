package indi.wenyan.interpreter.handler;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandler;

import java.util.Arrays;

public class TestHandler extends JavacallHandler {
    public TestHandler() {
        super();
    }
    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        System.out.println(Arrays.toString(args));
        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
