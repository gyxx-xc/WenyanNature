package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.WenyanCodes;

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
        exprVisitor.visit(ctx.data(0));
        exprVisitor.visit(ctx.data(1));
        bytecode.add(WenyanCodes.LOAD, ctx.if_logic_op().op.getText());
        bytecode.add(WenyanCodes.CALL, 2);
        return true;
    }

//    @Override
//    public WenyanValue visitFor_arr_statement(WenyanRParser.For_arr_statementContext ctx) {
//        WenyanValue value = new WenyanDataVisitor(bytecode).visit(ctx.data());
//        try {
//            value = WenyanValue.constOf(value).casting(WenyanValue.Type.LIST);
//        } catch (WenyanException.WenyanThrowException e) {
//            throw new WenyanException(e.getMessage(), ctx.data());
//        }
//        WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
//        for (WenyanValue item : (WenyanValue.WenyanValueArray) value.getValue()) {
//            functionEnvironment.setVariable(ctx.IDENTIFIER().getText(), item);
//            try {
//                for (WenyanRParser.StatementContext statementContext : ctx.statement()) {
//                    visitor.visit(statementContext);
//                }
//            } catch (BreakException e) {
//                break;
//            } catch (ContinueException ignored) { // i.e. continue
//            }
//        }
//        return null;
//    }

    @Override
    public Boolean visitFor_enum_statement(WenyanRParser.For_enum_statementContext ctx) {
        exprVisitor.visit(ctx.data());
        int forEnd = bytecode.getNewLabel();
        int progStart = bytecode.getNewLabel();
        bytecode.add(WenyanCodes.BRANCH_FALSE, forEnd);

        bytecode.setLabel(progStart);
        bodyVisitor.visit(ctx.program());

        bytecode.setProgEndLabel();
        bytecode.add(WenyanCodes.PUSH, new WenyanValue(WenyanValue.Type.INT, 1, true));
        bytecode.add(WenyanCodes.LOAD, "減");
        bytecode.add(WenyanCodes.CALL, 2);
        bytecode.add(WenyanCodes.BRANCH_FALSE, progStart);

        bytecode.setForEndLabel();
        bytecode.setLabel(forEnd);
        bytecode.add(WenyanCodes.POP);
        bytecode.exitFor();
        return null;
    }

    @Override
    public Boolean visitFor_while_statement(WenyanRParser.For_while_statementContext ctx) {
        bytecode.enterFor();
        int whileStart = bytecode.getNewLabel();

        bytecode.setLabel(whileStart);
        bodyVisitor.visit(ctx.program());

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
        bytecode.add(WenyanCodes.PUSH, (WenyanValue) null);
        bytecode.add(WenyanCodes.RET);
        return true;
    }
}
