package indi.wenyan.interpreter.compiler.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.runtime.WenyanStack;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.builtin.WenyanBuiltinFunction;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

/**
 * Visitor for handling expressions in Wenyan language.
 * Processes variable declarations, assignments, function definitions and calls,
 * object creation and member access.
 */
public class WenyanExprVisitor extends WenyanVisitor {
    /**
     * Delegate visitor for handling data expressions
     */
    private final WenyanDataVisitor dataVisitor = new WenyanDataVisitor(bytecode);

    /**
     * Constructs an expression visitor with the given bytecode environment
     * @param bytecode The compiler environment to emit bytecode to
     */
    public WenyanExprVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitReference_statement(WenyanRParser.Reference_statementContext ctx) {
        visit(ctx.data());
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitDeclare_statement(WenyanRParser.Declare_statementContext ctx) {
        int n;
        try {
            n = WenyanDataParser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        if (!ctx.d.isEmpty() && n != ctx.d.size()) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.variables_not_match").getString(), ctx);
        }
        WenyanType<?> type;
        try {
            type = WenyanDataParser.parseType(ctx.type().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        if (n > 100) { // STUB: change to variable
            throw new WenyanException(Component.translatable("error.wenyan_programming.too_many_variables").getString(), ctx);
        }
        for (int i = 0; i < n; i++) {
            if (ctx.d.isEmpty()) {
                bytecode.add(WenyanCodes.PUSH, IWenyanValue.emptyOf(type));
            } else {
                visit(ctx.d.get(i));
                bytecode.add(WenyanCodes.CAST, type.ordinal()); // STUB type
            }
            bytecode.add(WenyanCodes.PUSH_ANS);
        }
        return true;
    }

    @Override
    public Boolean visitInit_declare_statement(WenyanRParser.Init_declare_statementContext ctx) {
        try {
            visit(ctx.data());
            bytecode.add(WenyanCodes.CAST, WenyanDataParser.parseType(ctx.type().getText()).ordinal());
            bytecode.add(WenyanCodes.PUSH_ANS);
            return true;
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }

    @Override
    public Boolean visitDefine_statement(WenyanRParser.Define_statementContext ctx) {
        int n = ctx.definable_value().size();
        bytecode.add(WenyanCodes.PEEK_ANS_N, n);
        for (WenyanRParser.Definable_valueContext i : ctx.definable_value())
            visit(i);
        return true;
    }

    @Override
    public Boolean visitDefinable_value(WenyanRParser.Definable_valueContext ctx) {
        if (ctx.ZHI() != null) {
            bytecode.add(WenyanCodes.LOAD, ctx.SELF().getText());
            bytecode.add(WenyanCodes.STORE_ATTR, ctx.IDENTIFIER().getText());
        } else {
            bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER().getText());
        }
        return true;
    }

    @Override
    public Boolean visitAssign_data_statement(WenyanRParser.Assign_data_statementContext ctx) {
        visit(ctx.data(0)); // -> var
        visit(ctx.data(1)); // -> value
        bytecode.add(WenyanCodes.SET_VAR);
        return true;
    }

    @Override
    public Boolean visitAssign_null_statement(WenyanRParser.Assign_null_statementContext ctx) {
        visit(ctx.data());
        bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.SET_VAR);
        return true;
    }

    @Override
    public Boolean visitFunction_define_statement(WenyanRParser.Function_define_statementContext ctx) {
        if (!ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(ctx.IDENTIFIER().size() - 1).getText())) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_name_does_not_match").getString(), ctx);
        }
        visit(ctx.function_define_body());
        bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER(0).getText());
        return true;
    }

    @Override
    public Boolean visitFunction_define_body(WenyanRParser.Function_define_bodyContext ctx) {
        ArrayList<WenyanBuiltinFunction.Arg> argsType = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < ctx.args.size(); i++) {
            try {
                int n = WenyanDataParser.parseInt(ctx.args.get(i).getText());
                WenyanType<?> type = WenyanDataParser.parseType(ctx.t.get(i).getText());
                for (int j = 0; j < n; j++) {
                    argsType.add(new WenyanBuiltinFunction.Arg(type, ctx.id.get(count).getText()));
                    count++;
                }
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage(), ctx);
            }
        }

        WenyanBytecode functionBytecode = new WenyanBytecode();
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(functionBytecode);
        new WenyanMainVisitor(environment).visit(ctx.statements());

        // STUB: add a return null at end in case no return
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);

        bytecode.add(WenyanCodes.PUSH, new WenyanBuiltinFunction(argsType, functionBytecode));
        return true;
    }

    @Override
    public Boolean visitKey_function_call(WenyanRParser.Key_function_callContext ctx) {
        // args
        if (ctx.data().size() == 2) { // deal pp
            switch (ctx.pp.getFirst().getType()) {
                case WenyanRParser.PREPOSITION_RIGHT -> {
                    visit(ctx.data(1));
                    visit(ctx.data(0));
                }
                case WenyanRParser.PREPOSITION_LEFT -> {
                    visit(ctx.data(0));
                    visit(ctx.data(1));
                }
                default ->
                        throw new WenyanException(Component.translatable("error.wenyan_programming.unknown_preposition").getString(), ctx);
            }
        } else {
            for (int i = ctx.data().size() - 1; i >= 0; i--) {
                visit(ctx.data(i));
            }
        }

        // run
        bytecode.add(WenyanCodes.LOAD, ctx.key_function().op.getText());
        bytecode.add(WenyanCodes.CALL, ctx.data().size());
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitFunction_pre_call(WenyanRParser.Function_pre_callContext ctx) {
        for (int i = ctx.args.size() - 1; i >= 0; i--) {
            visit(ctx.args.get(i));
        }

        if (ctx.data(0) instanceof WenyanRParser.Data_childContext context) {
            visit(context.data());
            if (context.IDENTIFIER() != null)
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.IDENTIFIER().getText());
            else
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.CREATE_OBJECT().getText());
            bytecode.add(WenyanCodes.CALL_ATTR, ctx.args.size());
        } else {
            if (ctx.key_function() != null)
                bytecode.add(WenyanCodes.LOAD, ctx.key_function().op.getText());
            else
                visit(ctx.data(0));

            if (ctx.call.getType() == WenyanRParser.CREATE_OBJECT)
                bytecode.add(WenyanCodes.CAST, IWenyanObjectType.TYPE.ordinal());
            bytecode.add(WenyanCodes.CALL, ctx.args.size());
        }
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitFunction_post_call(WenyanRParser.Function_post_callContext ctx) {
        int count;
        try {
            count = WenyanDataParser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }

        if (count > WenyanStack.MAX_SIZE) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.too_many_variables").getString(), ctx);
        }
        for (int i = 0; i < count; i++)
            bytecode.add(WenyanCodes.POP_ANS);

        if (ctx.data() instanceof WenyanRParser.Data_childContext context) {
            visit(context.data());
            if (context.IDENTIFIER() != null)
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.IDENTIFIER().getText());
            else
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.CREATE_OBJECT().getText());
            bytecode.add(WenyanCodes.CALL_ATTR, count);
        } else {
            if (ctx.key_function() != null)
                bytecode.add(WenyanCodes.LOAD, ctx.key_function().op.getText());
            else
                visit(ctx.data());

            if (ctx.call.getType() == WenyanRParser.CREATE_OBJECT)
                bytecode.add(WenyanCodes.CAST, IWenyanObjectType.TYPE.ordinal());
            bytecode.add(WenyanCodes.CALL, count);
        }
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitObject_statement(WenyanRParser.Object_statementContext ctx) {
        if (!ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(ctx.IDENTIFIER().size() - 1).getText())) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_name_does_not_match").getString(), ctx);
        }

        if (ctx.data() != null) visit(ctx.data());
        else bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.CREATE_TYPE, ctx.IDENTIFIER(0).getText());

        try {
            for (WenyanRParser.Object_property_defineContext var : ctx.object_property_define()) {
                if (var.data() != null) {
                    visit(var.data());
                    bytecode.add(WenyanCodes.CAST, WenyanDataParser.parseType(var.type().getText()).ordinal());
                } else {
                    bytecode.add(WenyanCodes.PUSH, IWenyanValue.emptyOf(WenyanDataParser.parseType(var.type().getText())));
                }
                bytecode.add(WenyanCodes.STORE_STATIC_ATTR, var.IDENTIFIER().getText());
            }
        } catch (WenyanException.WenyanDataException e) {
            throw new WenyanException(e.getMessage());
        }

        for (WenyanRParser.Object_method_defineContext func : ctx.object_method_define()) {
            visit(func);
            if (func.IDENTIFIER().isEmpty())
                bytecode.add(WenyanCodes.STORE_FUNCTION_ATTR, func.CREATE_OBJECT(0).getText());
            else
                bytecode.add(WenyanCodes.STORE_FUNCTION_ATTR, func.IDENTIFIER(0).getText());
        }

        bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER(0).getText());
        return true;
    }

    @Override
    public Boolean visitObject_method_define(WenyanRParser.Object_method_defineContext ctx) {
        if ((!ctx.CREATE_OBJECT().isEmpty() && ctx.CREATE_OBJECT().size() != 2) ||
                (ctx.IDENTIFIER().size() == 2 && !ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(1).getText()))) {
            throw new WenyanException(Component.translatable("error.wenyan_programming.function_name_does_not_match").getString(), ctx);
        }

        visit(ctx.function_define_body());
        return true;
    }


    @Override
    public Boolean visitData_child(WenyanRParser.Data_childContext ctx) {
        return dataVisitor.visitData_child(ctx);
    }

    @Override
    public Boolean visitArray_index(WenyanRParser.Array_indexContext ctx) {
        return dataVisitor.visitArray_index(ctx);
    }

    @Override
    public Boolean visitId_last(WenyanRParser.Id_lastContext ctx) {
        return dataVisitor.visitId_last(ctx);
    }

    @Override
    public Boolean visitId_last_remain(WenyanRParser.Id_last_remainContext ctx) {
        return dataVisitor.visitId_last_remain(ctx);
    }

    @Override
    public Boolean visitLogic_data(WenyanRParser.Logic_dataContext ctx) {
        return dataVisitor.visitLogic_data(ctx);
    }

    @Override
    public Boolean visitId(WenyanRParser.IdContext ctx) {
        return dataVisitor.visitId(ctx);
    }

    @Override
    public Boolean visitData_primary(WenyanRParser.Data_primaryContext ctx) {
        return dataVisitor.visitData_primary(ctx);
    }

    @Override
    public Boolean visitSelf(WenyanRParser.SelfContext ctx) {
        return dataVisitor.visitSelf(ctx);
    }

    @Override
    public Boolean visitParent(WenyanRParser.ParentContext ctx) {
        return dataVisitor.visitParent(ctx);
    }
}
