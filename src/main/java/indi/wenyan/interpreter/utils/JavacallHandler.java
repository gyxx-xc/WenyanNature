package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.antlr.WenyanRParser;

/**
 * This class represents a Javacall context.
 * It makes itself as a fake statement context (program)
 */
public class JavacallHandler extends WenyanRParser.Function_define_statementContext {
    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }

    private final WenyanFunction function;

    public JavacallHandler() {
        this(null);
    }

    public JavacallHandler(WenyanFunction function) {
        super(null, 0);
        this.function = function;
    }

    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return function.apply(args);
    }
}