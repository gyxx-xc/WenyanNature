package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.network.chat.Component;

import java.util.concurrent.Semaphore;

public class WenyanMainVisitor extends WenyanVisitor {
    public WenyanMainVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore semaphore) {
        super(functionEnvironment, semaphore);
    }

    private void waitTick() {
        try {
            semaphore.acquire(2);
        } catch (InterruptedException e) {
            throw new WenyanException("killed");
        }
    }

    @Override
    public WenyanValue visitExpr_statement(WenyanRParser.Expr_statementContext ctx) {
        waitTick();
        return new WenyanExprVisitor(functionEnvironment, semaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitControl_statement(WenyanRParser.Control_statementContext ctx) {
        return new WenyanControlVisitor(functionEnvironment, semaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitCandy_statement(WenyanRParser.Candy_statementContext ctx) {
        waitTick();
        return new WenyanCandyVisitor(functionEnvironment, semaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitImport_statement(WenyanRParser.Import_statementContext ctx) {
        WenyanFunctionEnvironment importFunctionEnvironment =
                WenyanPackages.PACKAGES.get(ctx.STRING_LITERAL().getText());
        if (ctx.IDENTIFIER() == null) {
            functionEnvironment.importEnvironment(importFunctionEnvironment);
        } else {
            throw new WenyanException(Component.translatable("error.wenyan_nature.not_implemented").getString(), ctx);
        }
        return null;
    }
}
