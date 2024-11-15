package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.*;
import net.minecraft.network.chat.Component;

public class WenyanMainVisitor extends WenyanVisitor {
    public WenyanMainVisitor(WenyanFunctionEnvironment functionEnvironment, WenyanControl control) {
        super(functionEnvironment, control);
    }

    @Override
    public WenyanValue visitExpr_statement(WenyanRParser.Expr_statementContext ctx) {
        control.wait_tick();
        return new WenyanExprVisitor(functionEnvironment, control).visit(ctx);
    }

    @Override
    public WenyanValue visitControl_statement(WenyanRParser.Control_statementContext ctx) {
        return new WenyanControlVisitor(functionEnvironment, control).visit(ctx);
    }

    @Override
    public WenyanValue visitCandy_statement(WenyanRParser.Candy_statementContext ctx) {
        control.wait_tick();
        return new WenyanCandyVisitor(functionEnvironment, control).visit(ctx);
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
