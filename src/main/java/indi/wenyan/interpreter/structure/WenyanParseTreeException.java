package indi.wenyan.interpreter.structure;

import org.antlr.v4.runtime.ParserRuleContext;

public class WenyanParseTreeException extends RuntimeException {
    public WenyanParseTreeException(String message) {
        super(message);
    }

    public WenyanParseTreeException(String message, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + message);
    }

    public WenyanParseTreeException(WenyanThrowException e, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + e.getMessage());
    }

}
