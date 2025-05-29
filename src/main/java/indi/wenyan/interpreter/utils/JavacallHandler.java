package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanProgramCode;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.structure.WenyanException;

/**
 * This class represents a Javacall context.
 */
public class JavacallHandler extends WenyanProgramCode {
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

    protected static Object[] getArgs(WenyanValue[] args, WenyanValue.Type[] args_type) throws WenyanException.WenyanTypeException {
        Object[] newArgs = new Object[args.length];
        if (args.length != args_type.length)
            throw new WenyanException.WenyanTypeException("Argument length not match");
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].casting(args_type[i]).getValue();
        return newArgs;
    }

    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }
}
