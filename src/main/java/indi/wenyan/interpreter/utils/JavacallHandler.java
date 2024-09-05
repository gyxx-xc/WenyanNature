package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.antlr.WenyanRParser;

/**
 * This class represents a Javacall context.
 * It makes itself as a fake statement context (program)
 */
public abstract class JavacallHandler extends WenyanRParser.Function_define_statementContext {
    public JavacallHandler() {
        super(null, 0);
    }

    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        return null;
    }
}
