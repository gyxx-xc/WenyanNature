package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanRParser;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.utils.WenyanPackages;
import indi.wenyan.judou.utils.WenyanValues;
import org.antlr.v4.runtime.Token;

/**
 * Main visitor for Wenyan language that orchestrates other specialized visitors.
 * Delegates to appropriate visitor implementations based on statement type.
 */
public class WenyanMainVisitor extends WenyanVisitor {
    /**
     * Constructs a main visitor with the given bytecode environment
     * @param bytecode The compiler environment to emit bytecode to
     */
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
        bytecode.add(WenyanCodes.PUSH, WenyanValues.of(ctx.name.getText()));
        bytecode.add(WenyanCodes.LOAD, WenyanPackages.IMPORT_ID);
        bytecode.add(WenyanCodes.CALL, ctx.IDENTIFIER().size());
        // stack: id1, id2, ..., package, import
        for (Token id : ctx.prop) {
            bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, WenyanValues.of(id.getText()));
            bytecode.addStoreCode(id.getText());
        }
        return true;
    }
}
