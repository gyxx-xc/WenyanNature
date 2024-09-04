package indi.wenyan.handler;

import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanValue;

public class OutputHandler extends JavacallHandler {
    public OutputHandler() {
        super();
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) {
        System.out.println(args[0].toString());
        return null;
    }
}
