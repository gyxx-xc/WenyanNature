package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanBoolean;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

public class WenyanDataVisitor extends WenyanVisitor {
    public WenyanDataVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitData_primary(WenyanRParser.Data_primaryContext ctx) {
        try {
            IWenyanValue value = switch (ctx.data_type.getType()) {
                case WenyanRParser.BOOL_VALUE -> new WenyanBoolean(WenyanDataParser.parseBool(ctx.BOOL_VALUE().getText()));
                case WenyanRParser.INT_NUM -> new WenyanInteger(WenyanDataParser.parseInt(ctx.INT_NUM().getText()));
                case WenyanRParser.FLOAT_NUM -> new WenyanDouble(WenyanDataParser.parseFloat(ctx.FLOAT_NUM().getText()));
                case WenyanRParser.STRING_LITERAL -> new WenyanString(WenyanDataParser.parseString(ctx.STRING_LITERAL().getText()));
                default -> throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_data_type").getString(), ctx);
            };
            bytecode.add(WenyanCodes.PUSH, value);
            return true;
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e, ctx);
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
    public Boolean visitParent(WenyanRParser.ParentContext ctx) {
        bytecode.add(WenyanCodes.LOAD, ctx.PARENT().getText());
        return true;
    }

    @Override
    public Boolean visitArray_index(WenyanRParser.Array_indexContext ctx) {
        switch (ctx.p.getType()) {
            case WenyanRParser.INT_NUM -> {
                try {
                    bytecode.add(WenyanCodes.PUSH, new WenyanInteger(
                            WenyanDataParser.parseInt(ctx.INT_NUM().getText())));
                } catch (WenyanException.WenyanNumberException e) {
                    throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_number").getString(), ctx);
                }
            }
            case WenyanRParser.IDENTIFIER ->
                    bytecode.add(WenyanCodes.LOAD, ctx.IDENTIFIER().getText());
            case WenyanRParser.DATA_ID_LAST ->
                    bytecode.add(WenyanCodes.POP_ANS);
            default -> throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_data_type").getString(), ctx);
        }
        visit(ctx.data());
        bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, WenyanDataParser.ARRAY_GET_ID);
        bytecode.add(WenyanCodes.CALL_ATTR, 1);
        return true;
    }

    @Override
    public Boolean visitData_child(WenyanRParser.Data_childContext ctx) {
        boolean flag = bytecode.functionAttrFlag;
        bytecode.functionAttrFlag = false;
        visit(ctx.data());
        switch (ctx.p.getType()) {
            case WenyanRParser.LONG -> bytecode.add(WenyanCodes.LOAD_ATTR, ctx.LONG().getText());
            case WenyanRParser.STRING_LITERAL -> bytecode.add(flag ? WenyanCodes.LOAD_ATTR_REMAIN : WenyanCodes.LOAD_ATTR, ctx.STRING_LITERAL().getText());
            default -> throw new WenyanException(Component.translatable("error.wenyan_programming.invalid_data_type").getString(), ctx);
        }
        return true;
    }
}
