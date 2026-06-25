package indi.wenyan.judou.compiler.visitor;

import indi.wenyan.judou.antlr.WenyanParser;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.language.Symbol;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.exception.WenyanCompileException;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.ParsableType;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFunction;
import indi.wenyan.judou.utils.WenyanDataParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/// Visitor for handling expressions in Wenyan language.
/// Processes variable declarations, assignments, function definitions and calls,
/// object creation and member access.
public class WenyanExprVisitor extends WenyanVisitor {
    /// Delegate visitor for handling data expressions
    private final WenyanDataVisitor dataVisitor = new WenyanDataVisitor(bytecode);

    /// Constructs an expression visitor with the given bytecode environment
    ///
    /// @param bytecode The compiler environment to emit bytecode to
    public WenyanExprVisitor(WenyanCompilerEnvironment bytecode) {
        super(bytecode);
    }

    @Override
    public Boolean visitReference_statement(WenyanParser.Reference_statementContext ctx) {
        visit(ctx.data());
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitDeclare_statement(WenyanParser.Declare_statementContext ctx) {
        int n;
        try {
            n = WenyanDataParser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException e) {
            throw new WenyanCompileException(e.getMessage(), ctx);
        }
        if (n <= 0) {
            throw new WenyanCompileException(JudouExceptionText.VariablesNotPositive.string(), ctx);
        }
        if (n > WenyanCompilerEnvironment.FUNCTION_ARGS_MAX) {
            throw new WenyanCompileException(JudouExceptionText.TooManyVariables.string(), ctx);
        }
        if (!ctx.d.isEmpty() && n != ctx.d.size()) {
            throw new WenyanCompileException(JudouExceptionText.VariablesNotMatch.string(), ctx);
        }
        ParsableType type;
        try {
            type = WenyanDataParser.parseType(ctx.type().getText());
        } catch (WenyanException e) {
            throw new WenyanCompileException(e.getMessage(), ctx);
        }
        for (int i = 0; i < n; i++) {
            if (ctx.d.isEmpty()) {
                if (type == ParsableType.LIST) bytecode.add(WenyanCodes.CREATE_LIST);
                else {
                    try {
                        bytecode.add(WenyanCodes.PUSH, IWenyanValue.emptyOf(type));
                    } catch (WenyanException e) {
                        throw new WenyanCompileException(e.getMessage(), ctx);
                    }
                }
            } else {
                visit(ctx.d.get(i));
                bytecode.add(WenyanCodes.CAST, type.ordinal());
            }
            bytecode.add(WenyanCodes.PUSH_ANS);
        }
        return true;
    }

    @Override
    public Boolean visitInit_declare_statement(WenyanParser.Init_declare_statementContext ctx) {
        try {
            visit(ctx.data());
            bytecode.add(WenyanCodes.CAST, WenyanDataParser.parseType(ctx.type().getText()).ordinal());
            bytecode.add(WenyanCodes.PUSH_ANS);
            return true;
        } catch (WenyanException e) {
            throw new WenyanCompileException(e.getMessage(), ctx);
        }
    }

    @Override
    public Boolean visitDefine_statement(WenyanParser.Define_statementContext ctx) {
        int n = ctx.definable_value().size();
        bytecode.add(WenyanCodes.PEEK_ANS_N, n);
        for (WenyanParser.Definable_valueContext i : ctx.definable_value())
            visit(i);
        return true;
    }

    @Override
    public Boolean visitDefinable_value(WenyanParser.Definable_valueContext ctx) {
        if (ctx.ZHI() != null) {
            bytecode.addLoadCode(ctx.SELF().getText());
            bytecode.add(WenyanCodes.STORE_ATTR, ctx.IDENTIFIER().getText());
        } else {
            bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER().getText());
        }
        return true;
    }

    @Override
    public Boolean visitAssign_data_statement(WenyanParser.Assign_data_statementContext ctx) {
        visit(ctx.data(0)); // -> var
        visit(ctx.data(1)); // -> value
        bytecode.add(WenyanCodes.SET_VAR);
        return true;
    }

    @Override
    public Boolean visitAssign_null_statement(WenyanParser.Assign_null_statementContext ctx) {
        visit(ctx.data());
        bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.SET_VAR);
        return true;
    }

    @Override
    public Boolean visitAssign_simple_statement(WenyanParser.Assign_simple_statementContext ctx) {
        visit(ctx.data());
        bytecode.add(WenyanCodes.PEEK_ANS); // i.e. dataVisitor.visitId_last_remain()
        bytecode.add(WenyanCodes.SET_VAR);
        return true;
    }

    @Override
    public Boolean visitNamed_function_define(WenyanParser.Named_function_defineContext ctx) {
        if (!ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(ctx.IDENTIFIER().size() - 1).getText())) {
            throw new WenyanCompileException(JudouExceptionText.FunctionNameDoesNotMatch.string(), ctx);
        }
        // FIXME: a impl dependency here
        int index = bytecode.getStoreIndex(ctx.IDENTIFIER(0).getText());
        visitFunction_define_body(ctx.function_define_body(), false);
        bytecode.add(WenyanCodes.CREATE_FUNCTION, index);
        if (ctx.t.getType() == WenyanParser.ASYNC_DECLARE_OP) {
            bytecode.addLoadCode(Symbol.CREATE_ASYNC_ID);
            bytecode.add(WenyanCodes.CALL, 1);
        }
        bytecode.add(WenyanCodes.STORE, index);
        return true;
    }

    @Override
    public Boolean visitDeclared_lambda_function(WenyanParser.Declared_lambda_functionContext ctx) {
        visitLambda_function(ctx.lambda_function_body());
        // STUB: args stands for self's index for recursion
        //   add a -1 make itself never added to itself's scope
        bytecode.add(WenyanCodes.CREATE_FUNCTION, -1);
        if (ctx.declare.getType() == WenyanParser.ASYNC_DECLARE_OP) {
            bytecode.addLoadCode(Symbol.CREATE_ASYNC_ID);
            bytecode.add(WenyanCodes.CALL, 1);
        }
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitSimple_lambda_function(WenyanParser.Simple_lambda_functionContext ctx) {
        visitLambda_function(ctx.lambda_function_body());
        // STUB: see visitDeclared_lambda_function
        bytecode.add(WenyanCodes.CREATE_FUNCTION, -1);
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    private void visitFunction_define_body(WenyanParser.Function_define_bodyContext ctx, boolean isObject) {
        ArrayList<WenyanBuiltinFunction.Arg> argsType = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < ctx.args.size(); i++) {
            try {
                int n = WenyanDataParser.parseInt(ctx.args.get(i).getText());
                ParsableType type = WenyanDataParser.parseType(ctx.t.get(i).getText());
                for (int j = 0; j < n; j++) {
                    argsType.add(new WenyanBuiltinFunction.Arg(type, ctx.id.get(count).getText()));
                    count++;
                }
            } catch (WenyanException e) {
                throw new WenyanCompileException(e.getMessage(), ctx);
            }
        }

        addFunction(isObject, argsType, ctx.statements(), ctx);
    }

    private void visitLambda_function(WenyanParser.Lambda_function_bodyContext ctx) {
        ArrayList<WenyanBuiltinFunction.Arg> argsType = new ArrayList<>();
        for (int i = 0; i < ctx.id.size(); i++) {
            try {
                ParsableType type = null;
                if (!ctx.t.isEmpty()) type = WenyanDataParser.parseType(ctx.t.get(i).getText());
                argsType.add(new WenyanBuiltinFunction.Arg(type, ctx.id.get(i).getText()));
            } catch (WenyanException e) {
                throw new WenyanCompileException(e.getMessage(), ctx);
            }
        }

        addFunction(false, argsType, ctx.statements(), ctx);
    }

    private void addFunction(boolean isObject, ArrayList<WenyanBuiltinFunction.Arg> argsType,
                             WenyanParser.StatementsContext statements, ParserRuleContext body) {
        List<String> argv = new ArrayList<>();
        if (isObject) {
            argv.add(Symbol.SELF_ID);
            argv.add(Symbol.PARENT_ID);
        }
        for (var arg : argsType) argv.add(arg.id());
        WenyanCompilerEnvironment functionEnvironment = new WenyanCompilerEnvironment(bytecode.getSourceCode(), bytecode, argv, bytecode.isDebug());
        new WenyanMainVisitor(functionEnvironment).visit(statements);
        functionEnvironment.addAutoReturn(body);

        bytecode.add(WenyanCodes.PUSH, new WenyanBuiltinFunction(functionEnvironment.produceBytecode(), argsType, null));
    }

    @Override
    public Boolean visitKey_function_call(WenyanParser.Key_function_callContext ctx) {
        // args
        if (ctx.data().size() == 2) { // deal pp
            switch (ctx.pp.getFirst().getType()) {
                case WenyanParser.PREPOSITION_RIGHT -> {
                    visit(ctx.data(1));
                    visit(ctx.data(0));
                }
                case WenyanParser.PREPOSITION_LEFT -> {
                    visit(ctx.data(0));
                    visit(ctx.data(1));
                }
                default ->
                        throw new WenyanCompileException(JudouExceptionText.UnknownPreposition.string(), ctx);
            }
        } else {
            for (int i = ctx.data().size() - 1; i >= 0; i--) {
                visit(ctx.data(i));
            }
        }

        // run
        bytecode.addLoadCode(ctx.key_function().op.getText());
        bytecode.add(WenyanCodes.CALL, ctx.data().size());
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitFunction_pre_call(WenyanParser.Function_pre_callContext ctx) {
        for (int i = ctx.args.size() - 1; i >= 0; i--) {
            visit(ctx.args.get(i));
        }

        if (ctx.data(0) instanceof WenyanParser.Data_childContext context) {
            visit(context.data());
            if (context.IDENTIFIER() != null)
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.IDENTIFIER().getText());
            else
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.CREATE_OBJECT().getText());
            bytecode.add(WenyanCodes.CALL_ATTR, ctx.args.size());
        } else {
            if (ctx.key_function() != null)
                bytecode.addLoadCode(ctx.key_function().op.getText());
            else
                visit(ctx.data(0));

            if (ctx.call != null && ctx.call.getType() == WenyanParser.CREATE_OBJECT)
                bytecode.add(WenyanCodes.CAST, ParsableType.OBJECT_TYPE.ordinal());
            bytecode.add(WenyanCodes.CALL, ctx.args.size());
        }
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitFunction_post_call(WenyanParser.Function_post_callContext ctx) {
        int count;
        try {
            count = WenyanDataParser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException e) {
            throw new WenyanCompileException(e.getMessage(), ctx);
        }

        if (count > WenyanCompilerEnvironment.FUNCTION_ARGS_MAX) {
            throw new WenyanCompileException(JudouExceptionText.TooManyVariables.string(), ctx);
        }
        for (int i = 0; i < count; i++)
            bytecode.add(WenyanCodes.POP_ANS);

        if (ctx.data() instanceof WenyanParser.Data_childContext context) {
            visit(context.data());
            if (context.IDENTIFIER() != null)
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.IDENTIFIER().getText());
            else
                bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, context.CREATE_OBJECT().getText());
            bytecode.add(WenyanCodes.CALL_ATTR, count);
        } else {
            if (ctx.key_function() != null)
                bytecode.addLoadCode(ctx.key_function().op.getText());
            else
                visit(ctx.data());

            if (ctx.call != null && ctx.call.getType() == WenyanParser.CREATE_OBJECT)
                bytecode.add(WenyanCodes.CAST, ParsableType.OBJECT_TYPE.ordinal());
            bytecode.add(WenyanCodes.CALL, count);
        }
        bytecode.add(WenyanCodes.PUSH_ANS);
        return true;
    }

    @Override
    public Boolean visitObject_statement(WenyanParser.Object_statementContext ctx) {
        if (!ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(ctx.IDENTIFIER().size() - 1).getText())) {
            throw new WenyanCompileException(JudouExceptionText.FunctionNameDoesNotMatch.string(), ctx);
        }

        if (ctx.data() != null) visit(ctx.data());
        else bytecode.add(WenyanCodes.PUSH, WenyanNull.NULL);
        bytecode.add(WenyanCodes.CREATE_TYPE);

        try {
            for (WenyanParser.Object_property_defineContext variable : ctx.object_property_define()) {
                ParsableType type = WenyanDataParser.parseType(variable.type().getText());
                if (variable.data() != null) {
                    visit(variable.data());
                    bytecode.add(WenyanCodes.CAST, type.ordinal());
                } else {
                    if (type == ParsableType.LIST) bytecode.add(WenyanCodes.CREATE_LIST);
                    else bytecode.add(WenyanCodes.PUSH, IWenyanValue.emptyOf(type));
                }
                bytecode.add(WenyanCodes.STORE_STATIC_ATTR, variable.IDENTIFIER().getText());
            }
        } catch (WenyanException e) {
            throw new WenyanCompileException(e.getMessage(), ctx);
        }

        for (WenyanParser.Object_method_defineContext func : ctx.object_method_define()) {
            visit(func);
            bytecode.add(WenyanCodes.CREATE_FUNCTION, -1);
            if (func.IDENTIFIER().isEmpty())
                bytecode.add(WenyanCodes.STORE_FUNCTION_ATTR, func.CREATE_OBJECT(0).getText());
            else
                bytecode.add(WenyanCodes.STORE_FUNCTION_ATTR, func.IDENTIFIER(0).getText());
        }

        bytecode.add(WenyanCodes.STORE, ctx.IDENTIFIER(0).getText());
        return true;
    }

    @Override
    public Boolean visitObject_method_define(WenyanParser.Object_method_defineContext ctx) {
        if ((!ctx.CREATE_OBJECT().isEmpty() && ctx.CREATE_OBJECT().size() != 2) ||
                (ctx.IDENTIFIER().size() == 2 && !ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(1).getText()))) {
            throw new WenyanCompileException(JudouExceptionText.FunctionNameDoesNotMatch.string(), ctx);
        }

        visitFunction_define_body(ctx.function_define_body(), true);
        return true;
    }

    @Override
    public Boolean visitImport_statement(WenyanParser.Import_statementContext ctx) {
        bytecode.add(WenyanCodes.PUSH, WenyanValues.of(ctx.name.getText()));
        bytecode.addLoadCode(Symbol.IMPORT_ID);
        bytecode.add(WenyanCodes.CALL, 1);
        if (ctx.prop.isEmpty()) {
            bytecode.add(WenyanCodes.STORE, ctx.name.getText());
            return true;
        }
        // stack: id1, id2, ..., package, import
        for (Token id : ctx.prop) {
            bytecode.add(WenyanCodes.LOAD_ATTR_REMAIN, id.getText());
            bytecode.add(WenyanCodes.STORE, id.getText());
        }
        bytecode.add(WenyanCodes.POP);
        return true;
    }

    @Override
    public Boolean visitData_child(WenyanParser.Data_childContext ctx) {
        return dataVisitor.visitData_child(ctx);
    }

    @Override
    public Boolean visitArray_index(WenyanParser.Array_indexContext ctx) {
        return dataVisitor.visitArray_index(ctx);
    }

    @Override
    public Boolean visitId_last(WenyanParser.Id_lastContext ctx) {
        return dataVisitor.visitId_last(ctx);
    }

    @Override
    public Boolean visitId_last_remain(WenyanParser.Id_last_remainContext ctx) {
        return dataVisitor.visitId_last_remain(ctx);
    }

    @Override
    public Boolean visitLogic_data(WenyanParser.Logic_dataContext ctx) {
        return dataVisitor.visitLogic_data(ctx);
    }

    @Override
    public Boolean visitId(WenyanParser.IdContext ctx) {
        return dataVisitor.visitId(ctx);
    }

    @Override
    public Boolean visitData_primary(WenyanParser.Data_primaryContext ctx) {
        return dataVisitor.visitData_primary(ctx);
    }

    @Override
    public Boolean visitSelf(WenyanParser.SelfContext ctx) {
        return dataVisitor.visitSelf(ctx);
    }

    @Override
    public Boolean visitParent(WenyanParser.ParentContext ctx) {
        return dataVisitor.visitParent(ctx);
    }
}
