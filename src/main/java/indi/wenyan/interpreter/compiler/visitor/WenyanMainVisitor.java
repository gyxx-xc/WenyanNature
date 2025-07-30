package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanPackages;
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
        // stack: id1, id2, ..., package, import
        for (TerminalNode id : ctx.IDENTIFIER().subList(1, ctx.IDENTIFIER().size()))
            bytecode.add(WenyanCodes.PUSH, new WenyanString(id.getText()));
        bytecode.add(WenyanCodes.PUSH, new WenyanString(ctx.IDENTIFIER(0).getText()));
        bytecode.add(WenyanCodes.LOAD, WenyanPackages.IMPORT_ID);
        bytecode.add(WenyanCodes.CALL, ctx.IDENTIFIER().size());
        return true;
    }
}
