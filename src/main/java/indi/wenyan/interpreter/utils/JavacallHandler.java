package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;

/**
 * This class represents a Javacall context.
 */
public class JavacallHandler {
    private final WenyanFunction function;

    public JavacallHandler() {
        this(null);
    }

    public JavacallHandler(WenyanFunction function) {
        this.function = function;
    }

    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return function.apply(args);
    }

    public void handle(WenyanThread thread, WenyanValue[] args, boolean noReturn) throws WenyanException.WenyanThrowException {
        WenyanValue value = handle(args);
        if (!noReturn)
            thread.currentRuntime().processStack.push(value);
    }

    protected static Object[] getArgs(WenyanValue[] args, WenyanValue.Type[] args_type) throws WenyanException.WenyanTypeException {
        Object[] newArgs = new Object[args.length];
        if (args.length != args_type.length)
            throw new WenyanException.WenyanTypeException("Argument length not match");
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].casting(args_type[i]).getValue();
        return newArgs;
    }

    public boolean isLocal() {
        return true;
    }

    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }
}
