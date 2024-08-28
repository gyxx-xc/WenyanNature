package indi.wenyan.interpreter.visitor;


import indi.wenyan.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;

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
}
