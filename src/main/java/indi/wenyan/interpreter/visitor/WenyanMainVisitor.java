package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.WenyanCodes;
import org.antlr.v4.runtime.tree.TerminalNode;

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

    // STUB: might change
    @Override
    public Boolean visitImport_statement(WenyanRParser.Import_statementContext ctx) {
        if (ctx.IDENTIFIER().isEmpty()) {
            bytecode.add(WenyanCodes.IMPORT, ctx.STRING_LITERAL().getText());
        } else {
            bytecode.add(WenyanCodes.PUSH,
                    new WenyanValue(WenyanValue.Type.STRING, ctx.STRING_LITERAL().getText(), true));
            for (TerminalNode id : ctx.IDENTIFIER())
                bytecode.add(WenyanCodes.IMPORT_FROM, id.getText());
            bytecode.add(WenyanCodes.POP);
        }
        return true;
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
