package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;

/**
 * This class represents a Javacall context.
 */
public class JavacallHandler extends WenyanCode {
    private final WenyanFunction function;

    public JavacallHandler() {
        this(null);
    }

    public JavacallHandler(WenyanFunction function) {
        super("JAVA_CALL");
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

    @Override
    public void exec(int args, WenyanProgram program) {
        getStep(args, program);
        WenyanRuntime runtime = program.runtimes.peek();
        WenyanValue[] functionArgs = new WenyanValue[args];
        for(int i = 0; i < args; i++) {
            functionArgs[i] = runtime.processStack.pop();
        }
        WenyanValue value;
        try {
            value = function.apply(functionArgs);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
        if (!runtime.noReturnFlag)
            runtime.processStack.push(value);
    }

    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }
}
