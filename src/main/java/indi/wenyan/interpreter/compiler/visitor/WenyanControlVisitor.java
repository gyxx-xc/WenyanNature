package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanDataParser;

// this class is for
// flush_statement
// if_statement
// for_statement
// return_statement
// BREAK
public class WenyanControlVisitor extends WenyanVisitor {
    public WenyanControlVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    private final WenyanExprVisitor exprVisitor = new WenyanExprVisitor(bytecode);
    private final WenyanMainVisitor bodyVisitor = new WenyanMainVisitor(bytecode);

    @Override
    public Boolean visitFlush_statement(WenyanRParser.Flush_statementContext ctx) {
        bytecode.add(WenyanCodes.FLUSH);
        return true;
    }

    @Override
    public Boolean visitIf_statement(WenyanRParser.If_statementContext ctx) {
        visit(ctx.if_expression());
        int ifEnds = bytecode.getNewLabel();
        bytecode.add(WenyanCodes.BRANCH_POP_FALSE, ifEnds);
        bodyVisitor.visit(ctx.if_); // if body
        if (ctx.else_ == null) {
            bytecode.setLabel(ifEnds);
        } else {
            int elseEnds = bytecode.getNewLabel();
            bytecode.add(WenyanCodes.JMP, elseEnds);

            bytecode.setLabel(ifEnds);
            bodyVisitor.visit(ctx.else_);

            bytecode.setLabel(elseEnds);
        }
        return true;
    }

    @Override
    public Boolean visitIf_data(WenyanRParser.If_dataContext ctx) {
        exprVisitor.visit(ctx.data());
        return true;
    }

    @Override
    public Boolean visitIf_logic(WenyanRParser.If_logicContext ctx) {
        exprVisitor.visit(ctx.data(1));
        exprVisitor.visit(ctx.data(0));
        bytecode.add(WenyanCodes.LOAD, ctx.if_logic_op().op.getText());
        bytecode.add(WenyanCodes.CALL, 2);
        return true;
    }

    @Override
    public Boolean visitFor_arr_statement(WenyanRParser.For_arr_statementContext ctx) {
        bytecode.enterFor();
        exprVisitor.visit(ctx.data());
        bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, WenyanDataParser.ITER_ID);
        bytecode.add(WenyanCodes.CALL_ATTR, 0);
        int forEnd = bytecode.getNewLabel();
        int progStart = bytecode.getNewLabel();

        bytecode.setLabel(progStart);
        bytecode.add(WenyanCodes.FOR_ITER, forEnd);
        bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER().getText());
        bodyVisitor.visit(ctx.statements());

        bytecode.setProgEndLabel();
        bytecode.add(WenyanCodes.JMP, progStart);

        bytecode.setForEndLabel();
        bytecode.setLabel(forEnd);
        bytecode.exitFor();
        return true;
    }

    @Override
    public Boolean visitFor_enum_statement(WenyanRParser.For_enum_statementContext ctx) {
        bytecode.enterFor();
        exprVisitor.visit(ctx.data());
        int forEnd = bytecode.getNewLabel();
        int progStart = bytecode.getNewLabel();

        bytecode.setLabel(progStart);
        bytecode.add(WenyanCodes.FOR_NUM, forEnd);
        bodyVisitor.visit(ctx.statements());

        bytecode.setProgEndLabel();
        bytecode.add(WenyanCodes.JMP, progStart);

        bytecode.setForEndLabel();
        bytecode.setLabel(forEnd);
        bytecode.exitFor();
        return true;
    }

    @Override
    public Boolean visitFor_while_statement(WenyanRParser.For_while_statementContext ctx) {
        bytecode.enterFor();
        int whileStart = bytecode.getNewLabel();

        bytecode.setLabel(whileStart);
        bodyVisitor.visit(ctx.statements());

        bytecode.setProgEndLabel();
        bytecode.add(WenyanCodes.JMP, whileStart);

        bytecode.setForEndLabel();
        bytecode.exitFor();
        return true;
    }

    @Override
    public Boolean visitBreak_(WenyanRParser.Break_Context ctx) {
        bytecode.add(WenyanCodes.JMP, bytecode.getForEndLabel());
        return true;
    }

    @Override
    public Boolean visitContinue_(WenyanRParser.Continue_Context ctx) {
        bytecode.add(WenyanCodes.JMP, bytecode.getProgEndLabel());
        return true;
    }

    @Override
    public Boolean visitReturn_data_statement(WenyanRParser.Return_data_statementContext ctx) {
        exprVisitor.visit(ctx.data());
        bytecode.add(WenyanCodes.RET);
        return true;
    }

    @Override
    public Boolean visitReturn_last_statement(WenyanRParser.Return_last_statementContext ctx) {
        bytecode.add(WenyanCodes.POP_ANS);
        bytecode.add(WenyanCodes.RET);
        return true;
    }

    @Override
    public Boolean visitReturn_void_statement(WenyanRParser.Return_void_statementContext ctx) {
        bytecode.add(WenyanCodes.PUSH, new WenyanNativeValue(WenyanType.NULL, null, true));
        bytecode.add(WenyanCodes.RET);
        return true;
    }
}
