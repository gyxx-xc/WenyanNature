package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.structure.WenyanValue;

public class WenyanCandyVisitor extends WenyanVisitor {

    public WenyanCandyVisitor(WenyanFunctionEnvironment functionEnvironment, WenyanControl control) {
        super(functionEnvironment, control);
    }

    @Override
    public WenyanValue visitDeclare_write_candy_statement(WenyanRParser.Declare_write_candy_statementContext ctx) {
        return new WenyanExprVisitor(functionEnvironment, control).visitDeclare_write_candy_statement(ctx);
    }
}
