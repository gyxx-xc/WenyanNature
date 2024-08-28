package indi.wenyan.interpreter.visitor;

import indi.wenyan.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;

public class WenyanCandyVisitor extends WenyanVisitor{
    public WenyanCandyVisitor(WenyanFunctionEnvironment functionEnvironment) {
        super(functionEnvironment);
    }

    @Override
    public WenyanValue visitDeclare_write_candy_statement(WenyanRParser.Declare_write_candy_statementContext ctx) {
        return new WenyanExprVisitor(functionEnvironment).visitDeclare_write_candy_statement(ctx);
    }
}
