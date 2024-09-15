package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanDataPhaser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.network.chat.Component;

import java.util.concurrent.Semaphore;

public class WenyanDataVisitor extends WenyanVisitor {
    public WenyanDataVisitor(WenyanFunctionEnvironment functionEnvironment, Semaphore programSemaphore, Semaphore entitySemaphore) {
        super(functionEnvironment, programSemaphore, entitySemaphore);
    }

    @Override
    public WenyanValue visitData_primary(WenyanRParser.Data_primaryContext ctx) {
        try {
            return switch (ctx.data_type.getType()) {
                case WenyanRParser.BOOL_VALUE -> new WenyanValue(WenyanValue.Type.BOOL,
                        WenyanDataPhaser.parseBool(ctx.BOOL_VALUE().getText()), true);
                case WenyanRParser.INT_NUM -> new WenyanValue(WenyanValue.Type.INT,
                        WenyanDataPhaser.parseInt(ctx.INT_NUM().getText()), true);
                case WenyanRParser.FLOAT_NUM -> new WenyanValue(WenyanValue.Type.DOUBLE,
                        WenyanDataPhaser.parseFloat(ctx.FLOAT_NUM().getText()),
                        true);
                case WenyanRParser.STRING_LITERAL -> new WenyanValue(WenyanValue.Type.STRING,
                        WenyanDataPhaser.parseString(ctx.STRING_LITERAL().getText()), true);
                default -> throw new WenyanException(Component.translatable("error.wenyan_nature.unknown_data_type").getString(), ctx);
            };
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }

    @Override
    public WenyanValue visitId_last(WenyanRParser.Id_lastContext ctx) {
        WenyanValue value = this.functionEnvironment.resultStack.peek();
        if (value == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.last_result_is_null").getString(), ctx);
        functionEnvironment.resultStack.empty();
        return value;
    }

    @Override
    public WenyanValue visitId(WenyanRParser.IdContext ctx) {
        String id = ctx.IDENTIFIER().getText();
        try {
            return functionEnvironment.getVariable(id);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found:_").getString() + id, ctx);
        }
    }

    // TODO: maybe refactor this by calling function
    @Override
    public WenyanValue visitData_child(WenyanRParser.Data_childContext ctx) {
        WenyanValue parent = visit(ctx.data());
        WenyanValue value;
        try {
            switch (ctx.p.getType()) {
                case WenyanRParser.INT_NUM -> value = new WenyanValue(WenyanValue.Type.INT,
                        WenyanDataPhaser.parseInt(ctx.INT_NUM().getText()), true);
                case WenyanRParser.IDENTIFIER -> {
                    try {
                        value = functionEnvironment
                                .getVariable(ctx.IDENTIFIER().getText())
                                .casting(WenyanValue.Type.INT);
                    } catch (WenyanException.WenyanThrowException e) {
                        throw new WenyanException(e.getMessage(), ctx);
                    }
                }
                case WenyanRParser.DATA_ID_LAST -> {
                    value = this.functionEnvironment.resultStack.peek().casting(WenyanValue.Type.INT);
                    if (value == null)
                        throw new WenyanException(Component.translatable("error.wenyan_nature.last_result_is_null").getString(), ctx);
                    functionEnvironment.resultStack.empty();
                }
                case WenyanRParser.LONG -> {
                    return new WenyanValue(WenyanValue.Type.INT,
                            ((WenyanValue.WenyanValueArray) parent.getValue()).size(), true);
                }
                default -> throw new WenyanException(Component.translatable("error.wenyan_nature.unknown_data_type").getString(), ctx);
            }
            return ((WenyanValue.WenyanValueArray) parent.getValue()).get(value);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }
}
