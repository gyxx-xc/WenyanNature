package indi.wenyan.interpreter.structure;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Base exception class for Wenyan interpreter errors
 */
public class WenyanException extends WenyanThrowException {
    public WenyanException(String message) {
        super(message);
    }

    public WenyanException(String message, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + message);
    }

    public WenyanException(WenyanThrowException e, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + e.getMessage());
    }

    public static class WenyanUnreachedException extends WenyanException {
        public WenyanUnreachedException() {
            super("unreached, please report an issue");
        }
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

    /**
     * Exception for runtime errors
     */
    public static class WenyanWarperError extends WenyanException {
        public final Throwable cause;
        public WenyanWarperError(Throwable e) {
            super(e.getMessage());
            cause = e;
        }
    }
}
