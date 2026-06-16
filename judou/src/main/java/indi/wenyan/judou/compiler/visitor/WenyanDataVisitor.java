package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanParser;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.Symbol;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanCompileException;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.utils.WenyanDataParser;

/**
 * Visitor for handling data expressions and literals in Wenyan language.
 * Responsible for compiling primitive values, identifiers, array access,
 * and object property access.
 */
public class WenyanDataVisitor extends WenyanVisitor {
    /**
     * Constructs a data visitor with the given bytecode environment
     * @param bytecode The compiler environment to emit bytecode to
     */
    public WenyanDataVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitData_primary(WenyanParser.Data_primaryContext ctx) {
        try {
            IWenyanValue value = switch (ctx.data_type.getType()) {
                case WenyanParser.BOOL_VALUE -> WenyanValues.of(WenyanDataParser.parseBool(ctx.BOOL_VALUE().getText()));
                case WenyanParser.INT_NUM -> WenyanDataParser.parseWyInt(ctx.INT_NUM().getText());
                case WenyanParser.FLOAT_NUM -> WenyanValues.of(WenyanDataParser.parseFloat(ctx.FLOAT_NUM().getText()));
                case WenyanParser.STRING_LITERAL -> WenyanValues.of(WenyanDataParser.parseString(ctx.STRING_LITERAL().getText()));
                default -> throw new WenyanCompileException(JudouExceptionText.InvalidDataType.string(), ctx);
            };
            bytecode.add(WenyanCodes.PUSH, value);
            return true;
        } catch (WenyanException e) {
            throw new WenyanCompileException(e, ctx);
        }
    }

    @Override
    public Boolean visitId_last(WenyanParser.Id_lastContext ctx) {
        bytecode.add(WenyanCodes.POP_ANS);
        return true;
    }

    @Override
    public Boolean visitId_last_remain(WenyanParser.Id_last_remainContext ctx) {
        bytecode.add(WenyanCodes.PEEK_ANS);
        return true;
    }

    @Override
    public Boolean visitId(WenyanParser.IdContext ctx) {
        bytecode.addLoadCode(ctx.IDENTIFIER().getText());
        return true;
    }

    @Override
    public Boolean visitSelf(WenyanParser.SelfContext ctx) {
        bytecode.addLoadCode(ctx.SELF().getText());
        return true;
    }

    @Override
    public Boolean visitLogic_data(WenyanParser.Logic_dataContext ctx) {
        visit(ctx.data(1));
        visit(ctx.data(0));
        bytecode.addLoadCode(ctx.if_logic_op().op.getText());
        bytecode.add(WenyanCodes.CALL, 2);
        return true;
    }

    @Override
    public Boolean visitParent(WenyanParser.ParentContext ctx) {
        bytecode.addLoadCode(ctx.PARENT().getText());
        return true;
    }

    @Override
    public Boolean visitArray_index(WenyanParser.Array_indexContext ctx) {
        switch (ctx.p.getType()) {
            case WenyanParser.INT_NUM -> {
                try {
                    bytecode.add(WenyanCodes.PUSH, WenyanValues.of(
                            WenyanDataParser.parseInt(ctx.INT_NUM().getText())));
                } catch (WenyanException.WenyanNumberException e) {
                    throw new WenyanCompileException(JudouExceptionText.InvalidNumber.string(), ctx);
                }
            }
            case WenyanParser.DATA_ID_LAST ->
                    bytecode.add(WenyanCodes.POP_ANS);
            default -> throw new WenyanCompileException(JudouExceptionText.InvalidDataType.string(), ctx);
        }
        visit(ctx.data());
        bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, Symbol.ARRAY_GET_ID);
        bytecode.add(WenyanCodes.CALL_ATTR, 1);
        return true;
    }

    @Override
    public Boolean visitData_child(WenyanParser.Data_childContext ctx) {
        visit(ctx.data());
        switch (ctx.p.getType()) {
            case WenyanParser.LONG -> bytecode.add(WenyanCodes.LOAD_ATTR, ctx.LONG().getText());
            case WenyanParser.IDENTIFIER -> bytecode.add(WenyanCodes.LOAD_ATTR, ctx.IDENTIFIER().getText());
            default -> throw new WenyanCompileException(JudouExceptionText.InvalidDataType.string(), ctx);
        }
        return true;
    }
}
