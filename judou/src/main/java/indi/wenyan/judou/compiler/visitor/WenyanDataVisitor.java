package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanRParser;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanParseTreeException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.WenyanDataParser;
import indi.wenyan.judou.utils.WenyanValues;

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
    public Boolean visitData_primary(WenyanRParser.Data_primaryContext ctx) {
        try {
            IWenyanValue value = switch (ctx.data_type.getType()) {
                case WenyanRParser.BOOL_VALUE -> WenyanValues.of(WenyanDataParser.parseBool(ctx.BOOL_VALUE().getText()));
                case WenyanRParser.INT_NUM -> WenyanValues.of(WenyanDataParser.parseInt(ctx.INT_NUM().getText()));
                case WenyanRParser.FLOAT_NUM -> WenyanValues.of(WenyanDataParser.parseFloat(ctx.FLOAT_NUM().getText()));
                case WenyanRParser.STRING_LITERAL -> WenyanValues.of(WenyanDataParser.parseString(ctx.STRING_LITERAL().getText()));
                default -> throw new WenyanParseTreeException(LanguageManager.getTranslation("error.wenyan_programming.invalid_data_type"), ctx);
            };
            bytecode.add(WenyanCodes.PUSH, value);
            return true;
        } catch (WenyanException e) {
            throw new WenyanParseTreeException(e, ctx);
        }
    }

    @Override
    public Boolean visitId_last(WenyanRParser.Id_lastContext ctx) {
        bytecode.add(WenyanCodes.POP_ANS);
        return true;
    }

    @Override
    public Boolean visitId_last_remain(WenyanRParser.Id_last_remainContext ctx) {
        bytecode.add(WenyanCodes.PEEK_ANS);
        return true;
    }

    @Override
    public Boolean visitId(WenyanRParser.IdContext ctx) {
        bytecode.add(WenyanCodes.LOAD, ctx.IDENTIFIER().getText());
        return true;
    }

    @Override
    public Boolean visitSelf(WenyanRParser.SelfContext ctx) {
        bytecode.add(WenyanCodes.LOAD, ctx.SELF().getText());
        return true;
    }

    @Override
    public Boolean visitLogic_data(WenyanRParser.Logic_dataContext ctx) {
        visit(ctx.data(1));
        visit(ctx.data(0));
        bytecode.add(WenyanCodes.LOAD, ctx.if_logic_op().op.getText());
        bytecode.add(WenyanCodes.CALL, 2);
        return true;
    }

    @Override
    public Boolean visitParent(WenyanRParser.ParentContext ctx) {
        bytecode.add(WenyanCodes.LOAD, ctx.PARENT().getText());
        return true;
    }

    @Override
    public Boolean visitArray_index(WenyanRParser.Array_indexContext ctx) {
        switch (ctx.p.getType()) {
            case WenyanRParser.INT_NUM -> {
                try {
                    bytecode.add(WenyanCodes.PUSH, WenyanValues.of(
                            WenyanDataParser.parseInt(ctx.INT_NUM().getText())));
                } catch (WenyanException.WenyanNumberException e) {
                    throw new WenyanParseTreeException(LanguageManager.getTranslation("error.wenyan_programming.invalid_number"), ctx);
                }
            }
            case WenyanRParser.DATA_ID_LAST ->
                    bytecode.add(WenyanCodes.POP_ANS);
            default -> throw new WenyanParseTreeException(LanguageManager.getTranslation("error.wenyan_programming.invalid_data_type"), ctx);
        }
        visit(ctx.data());
        bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, WenyanDataParser.ARRAY_GET_ID);
        bytecode.add(WenyanCodes.CALL_ATTR, 1);
        return true;
    }

    @Override
    public Boolean visitData_child(WenyanRParser.Data_childContext ctx) {
        visit(ctx.data());
        switch (ctx.p.getType()) {
            case WenyanRParser.LONG -> bytecode.add(WenyanCodes.LOAD_ATTR, ctx.LONG().getText());
            case WenyanRParser.IDENTIFIER -> bytecode.add(WenyanCodes.LOAD_ATTR, ctx.IDENTIFIER().getText());
            default -> throw new WenyanParseTreeException(LanguageManager.getTranslation("error.wenyan_programming.invalid_data_type"), ctx);
        }
        return true;
    }
}
