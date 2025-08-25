package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanDataParser;

// this class is for
// flush_statement
// if_statement
// for_statement
// return_statement
// BREAK

/**
 * Visitor for handling control flow statements in Wenyan language.
 * Processes control structures such as:
 * - flush statements
 * - if-else conditionals
 * - for/while loops
 * - return statements
 * - break/continue statements
 */
public class WenyanControlVisitor extends WenyanVisitor {
    /**
     * Visitor for handling expressions in control statements
     */
    private final WenyanExprVisitor exprVisitor = new WenyanExprVisitor(bytecode);

    /**
     * Visitor for handling statement bodies in control structures
     */
    private final WenyanMainVisitor bodyVisitor = new WenyanMainVisitor(bytecode);

    /**
     * Constructs a control visitor with the given bytecode environment
     *
     * @param bytecode The compiler environment to emit bytecode to
     */
    public WenyanControlVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitFlush_statement(WenyanRParser.Flush_statementContext ctx) {
        bytecode.add(WenyanCodes.FLUSH);
        return true;
    }

    @Override
    public Boolean visitIf_statement(WenyanRParser.If_statementContext ctx) {
        exprVisitor.visit(ctx.data());
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
        bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.RET);
        return true;
    }
}
