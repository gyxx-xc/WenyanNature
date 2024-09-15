package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;

import java.util.concurrent.Semaphore;

public class WenyanCandyVisitor extends WenyanVisitor{
    public WenyanCandyVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore programSemaphore, Semaphore entitySemaphore) {
        super(functionEnvironment, programSemaphore, entitySemaphore);
    }

    @Override
    public WenyanValue visitDeclare_write_candy_statement(WenyanRParser.Declare_write_candy_statementContext ctx) {
        return new WenyanExprVisitor(functionEnvironment, programSemaphore, entitySemaphore).visitDeclare_write_candy_statement(ctx);
    }
}
