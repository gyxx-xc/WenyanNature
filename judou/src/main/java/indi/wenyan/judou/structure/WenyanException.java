package indi.wenyan.judou.structure;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Base exception class for Wenyan interpreter errors
 */
public class WenyanException extends Exception {
    public WenyanException(String message) {
        super(message);
    }

    public WenyanException(String message, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + message);
    }

    public WenyanException(WenyanException e, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + e.getMessage());
    }

    /**
     * Exception for numerical errors
     */
    public static class WenyanNumberException extends WenyanException {
        public WenyanNumberException(String message) {
            super(message);
        }
    }

    /**
     * Exception for data handling errors
     */
    public static class WenyanDataException extends WenyanException {
        public WenyanDataException(String message) {
            super(message);
        }
    }

    /**
     * Exception for variable errors
     */
    public static class WenyanVarException extends WenyanException {
        public WenyanVarException(String message) {
            super(message);
        }
    }

    /**
     * Exception for type errors
     */
    public static class WenyanTypeException extends WenyanException {
        public WenyanTypeException(String message) {
            super(message);
        }
    }

    /**
     * Exception for code validation errors
     */
    public static class WenyanCheckerError extends WenyanException {
        public WenyanCheckerError(String message) {
            super(message);
        }
    }
}
