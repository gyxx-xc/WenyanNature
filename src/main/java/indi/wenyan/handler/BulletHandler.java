package indi.wenyan.handler;

import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanValue;

public class BulletHandler extends JavacallHandler {
    @Override
    public WenyanValue handle(WenyanValue[] args) {
        StringBuilder result = new StringBuilder();
        for (WenyanValue arg : args) {
            result.append(result.isEmpty() ? "" : " ").append(arg.toString());
        }
        return null;
    }
}
