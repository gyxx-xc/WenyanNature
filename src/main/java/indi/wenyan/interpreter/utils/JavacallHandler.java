package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;

/**
 * This class represents a Javacall context.
 */
public abstract class JavacallHandler {

    public abstract WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException;

    /**
     * Decided if this handler is running at program thread.
     * <p>
     * the handler will be executed in the main thread of MC if it is not local,
     * This is important since the MC is not thread-safe,
     * and can cause strange bug and unmatched exception, making it really hard to debug.
     *
     * @return true if local, false otherwise
     */
    public abstract boolean isLocal();

    /**
     * The step of this handler.
     * <p>
     * It can be decided by the feature of the handler for game balance,
     * e.g. powerful handler may take more time to execute.
     * However, it's better to keep the handler time not longer than O(step),
     * which may cause the program to be stuck.
     *
     * @return the step of this handler
     */
    @SuppressWarnings("unused")
    public int getStep(int args, WenyanThread thread) {
        return 1;
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

    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }
}
