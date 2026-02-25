package indi.wenyan.judou.structure;

import org.antlr.v4.runtime.ParserRuleContext;
import org.jetbrains.annotations.Nullable;

public class WenyanCompileException extends RuntimeException {
    @Nullable
    final Throwable cause;
    @Nullable
    final transient ParserRuleContext ctx;

    public WenyanCompileException(String message) {
        this(message, null, null);
    }

    public WenyanCompileException(String message, @Nullable Throwable cause, @Nullable ParserRuleContext ctx) {
        super(message);
        this.cause = cause;
        this.ctx = ctx;
    }

    public WenyanCompileException(String message, ParserRuleContext ctx) {
        this(message, null, ctx);
    }

    public WenyanCompileException(WenyanException e, ParserRuleContext ctx) {
        this(e.getMessage(), e, ctx);
    }

    @Override
    public String getMessage() {
        if (ctx != null)
            return ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + super.getMessage();
        else
            return super.getMessage();
    }
}
