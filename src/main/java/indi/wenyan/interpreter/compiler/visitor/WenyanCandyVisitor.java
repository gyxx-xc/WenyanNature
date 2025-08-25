package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.network.chat.Component;

/**
 * Visitor for handling candy statements in the Wenyan language.
 * Deals with special syntactic sugar constructs such as combined declarations
 * with write operations and boolean algebra.
 */
public class WenyanCandyVisitor extends WenyanVisitor {

    /**
     * Expression visitor for evaluating expressions within candy statements
     */
    private final WenyanExprVisitor exprVisitor = new WenyanExprVisitor(bytecode);

    /**
     * Constructs a candy visitor with the given bytecode environment
     * @param bytecode The compiler environment to emit bytecode to
     */
    public WenyanCandyVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitDeclare_write_candy_statement(WenyanRParser.Declare_write_candy_statementContext ctx) {
        exprVisitor.visit(ctx.declare_statement());
        try {
            int n = WenyanDataParser.parseInt(ctx.declare_statement().INT_NUM().getText());
            bytecode.add(WenyanCodes.PEEK_ANS_N, n);
            bytecode.add(WenyanCodes.LOAD, ctx.WRITE_KEY_FUNCTION().getText());
            bytecode.add(WenyanCodes.CALL, n);
            bytecode.add(WenyanCodes.PUSH_ANS);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        return true;
    }

    @Override
    public Boolean visitBoolean_algebra_statement(WenyanRParser.Boolean_algebra_statementContext ctx) {
        exprVisitor.visit(ctx.data(0));
        bytecode.add(WenyanCodes.CAST, WenyanBoolean.TYPE.ordinal());
        exprVisitor.visit(ctx.data(1));
        bytecode.add(WenyanCodes.CAST, WenyanBoolean.TYPE.ordinal());

        switch (ctx.op.getType()) {
            case WenyanRParser.AND -> bytecode.add(WenyanCodes.LOAD, WenyanPackages.AND_ID);
            case WenyanRParser.OR -> bytecode.add(WenyanCodes.LOAD, WenyanPackages.OR_ID);
            default ->
                    throw new WenyanException(Component.translatable("error.wenyan_programming.unknown_operator").getString(), ctx);
        }
        bytecode.add(WenyanCodes.CALL, 2);
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitMod_math_statement(WenyanRParser.Mod_math_statementContext ctx) {
        switch (ctx.pp.getType()) {
            case WenyanRParser.PREPOSITION_RIGHT:
                exprVisitor.visit(ctx.data(1));
                exprVisitor.visit(ctx.data(0));
                break;
            case WenyanRParser.PREPOSITION_LEFT:
                exprVisitor.visit(ctx.data(0));
                exprVisitor.visit(ctx.data(1));
                break;
            default:
                throw new WenyanException(Component.translatable("error.wenyan_programming.unknown_preposition").getString(), ctx);
        }
        bytecode.add(WenyanCodes.LOAD, WenyanPackages.MOD_ID);
        bytecode.add(WenyanCodes.CALL, 2);
        bytecode.add(WenyanCodes.PUSH_ANS);
        return null;
    }
}
