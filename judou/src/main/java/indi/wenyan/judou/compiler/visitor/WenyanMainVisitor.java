package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanRParser;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.values.WenyanNull;

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

    @Override
    public Boolean visitProgram(WenyanRParser.ProgramContext ctx) {
        visit(ctx.statements());
        bytecode.enterContext(ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine(),
                ctx.getStop().getStartIndex(), ctx.getStop().getStopIndex() + 1);
        bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.RET);
        bytecode.exitContext();
        return true;
    }
}
