package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.*;

public class WenyanMainVisitor extends WenyanVisitor {
    public WenyanMainVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitExpr_statement(WenyanRParser.Expr_statementContext ctx) {
        return new WenyanExprVisitor(bytecode).visit(ctx);
    }


    @Override
    public Boolean visitCandy_statement(WenyanRParser.Candy_statementContext ctx) {
        return new WenyanCandyVisitor(bytecode).visit(ctx);
    }

    @Override
    public Boolean visitControl_statement(WenyanRParser.Control_statementContext ctx) {
        return new WenyanControlVisitor(bytecode).visit(ctx);
    }

//    @Override
//    public WenyanValue visitImport_statement(WenyanRParser.Import_statementContext ctx) {
//        WenyanRuntime importFunctionEnvironment =
//                WenyanPackages.PACKAGES.get(ctx.STRING_LITERAL().getText());
//        if (ctx.IDENTIFIER().isEmpty()) {
//            functionEnvironment.importEnvironment(importFunctionEnvironment);
//        } else {
//            throw new WenyanException(Component.translatable("error.wenyan_nature.not_implemented").getString(), ctx);
//        }
//        return null;
//    }
}
