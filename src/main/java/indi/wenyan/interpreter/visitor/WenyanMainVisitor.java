package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.network.chat.Component;

public class WenyanMainVisitor extends WenyanVisitor {
    public WenyanMainVisitor(WenyanFunctionEnvironment functionEnvironment) {
        super(functionEnvironment);
    }

    public WenyanMainVisitor() {
        super(new WenyanFunctionEnvironment());
    }

    @Override
    public WenyanValue visitExpr_statement(WenyanRParser.Expr_statementContext ctx) {
        return new WenyanExprVisitor(functionEnvironment).visit(ctx);
    }

    @Override
    public WenyanValue visitControl_statement(WenyanRParser.Control_statementContext ctx) {
        return new WenyanControlVisitor(functionEnvironment).visit(ctx);
    }

    @Override
    public WenyanValue visitCandy_statement(WenyanRParser.Candy_statementContext ctx) {
        return new WenyanCandyVisitor(functionEnvironment).visit(ctx);
    }

    @Override
    public WenyanValue visitImport_statement(WenyanRParser.Import_statementContext ctx) {
        WenyanFunctionEnvironment importFunctionEnvironment =
                WenyanPackages.PACKAGES.get(ctx.STRING_LITERAL().getText());
        if (ctx.IDENTIFIER() == null) {
            functionEnvironment.importEnvironment(importFunctionEnvironment);
        } else {
            throw new RuntimeException(Component.translatable("error.wenyan_nature.not_implemented").getString());
        }
        return null;
    }
}
