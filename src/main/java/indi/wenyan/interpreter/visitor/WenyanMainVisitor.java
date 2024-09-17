package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.network.chat.Component;

import java.util.concurrent.Semaphore;

public class WenyanMainVisitor extends WenyanVisitor {
    public WenyanMainVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore programSemaphore, Semaphore entitySemaphore) {
        super(functionEnvironment, programSemaphore, entitySemaphore);
    }

    private void waitTick() {
        entitySemaphore.release(1);
        try {
            programSemaphore.acquire(1);
        } catch (InterruptedException e) {
            throw new WenyanException("killed");
        }
    }

    @Override
    public WenyanValue visitExpr_statement(WenyanRParser.Expr_statementContext ctx) {
        waitTick();
        return new WenyanExprVisitor(functionEnvironment, programSemaphore, entitySemaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitControl_statement(WenyanRParser.Control_statementContext ctx) {
        return new WenyanControlVisitor(functionEnvironment, programSemaphore, entitySemaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitCandy_statement(WenyanRParser.Candy_statementContext ctx) {
        waitTick();
        return new WenyanCandyVisitor(functionEnvironment, programSemaphore, entitySemaphore).visit(ctx);
    }

    @Override
    public WenyanValue visitImport_statement(WenyanRParser.Import_statementContext ctx) {
        WenyanFunctionEnvironment importFunctionEnvironment =
                WenyanPackages.PACKAGES.get(ctx.STRING_LITERAL().getText());
        if (ctx.IDENTIFIER().isEmpty()) {
            functionEnvironment.importEnvironment(importFunctionEnvironment);
        } else {
            throw new WenyanException(Component.translatable("error.wenyan_nature.not_implemented").getString(), ctx);
        }
        return null;
    }
}
