package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanDataPhaser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.utils.WenyanValue;

import java.util.ArrayList;
import java.util.List;

public class WenyanExprVisitor extends WenyanVisitor{
    public WenyanExprVisitor(WenyanFunctionEnvironment functionEnvironment) {
        super(functionEnvironment);
    }

    // maybe it is better to use a function to push return value...
    @Override
    public WenyanValue visitReference_statement(WenyanRParser.Reference_statementContext ctx) {
        WenyanValue value = new WenyanDataVisitor(functionEnvironment).visit(ctx.data());
        return functionEnvironment.resultStack.push(value);
    }

    @Override
    public WenyanValue visitDeclare_statement(WenyanRParser.Declare_statementContext ctx) {
        int n;
        try {
            n = WenyanDataPhaser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        if (!ctx.d.isEmpty() && n != ctx.d.size()) {
            System.out.println(ctx.getText());
            throw new WenyanException("number of variables does not match number of values", ctx);
        }
        WenyanValue.Type type;
        try {
            type = WenyanDataPhaser.parseType(ctx.type().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        for (int i = 0; i < n; i++) {
            try {
                if (!ctx.d.isEmpty()) {
                    WenyanValue value = (new WenyanDataVisitor(functionEnvironment)).visit(ctx.d.get(i));
                    functionEnvironment.resultStack.push(WenyanValue.constOf(value).casting(type));
                } else {
                    functionEnvironment.resultStack.push(WenyanValue.emptyOf(WenyanDataPhaser.parseType(ctx.type().getText()), true));
                }
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage(), ctx);
            }
        }
        return functionEnvironment.resultStack.peek();
    }

    @Override
    public WenyanValue visitInit_declare_statement(WenyanRParser.Init_declare_statementContext ctx) {
        WenyanValue value = (new WenyanDataVisitor(functionEnvironment)).visit(ctx.data());
        try {
            return functionEnvironment.resultStack.push(WenyanValue.constOf(value)
                    .casting(WenyanDataPhaser.parseType(ctx.type().getText())));
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }

    @Override
    public WenyanValue visitDefine_statement(WenyanRParser.Define_statementContext ctx) {
        int n = ctx.d.size();
        for (int i = 0; i < n; i ++) {
            functionEnvironment.setVariable(ctx.d.get(i).getText(),
                    WenyanValue.varOf(functionEnvironment.resultStack.get(functionEnvironment.resultStack.size() - n + i)));
        }
        return functionEnvironment.resultStack.peek();
    }

    @Override
    public WenyanValue visitAssign_data_statement(WenyanRParser.Assign_data_statementContext ctx) {
        WenyanValue var = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(0));
        if (var.isConst())
            throw new WenyanException("cannot assign to constant", ctx);
        WenyanValue value = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(1));
        // although the constOf do nothing here,
        // it is better to keep the code consistent
        try {
            var.setValue(WenyanValue.constOf(value).casting(var.getType()).getValue());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        return functionEnvironment.resultStack.push(var);
    }

    @Override
    public WenyanValue visitAssign_null_statement(WenyanRParser.Assign_null_statementContext ctx) {
        WenyanValue var = new WenyanDataVisitor(functionEnvironment).visit(ctx.data());
        if (var.isConst())
            throw new WenyanException("cannot assign to constant", ctx);
        var.setValue(null);
        return functionEnvironment.resultStack.push(null);
    }

    @Override
    public WenyanValue visitBoolean_algebra_statement(WenyanRParser.Boolean_algebra_statementContext ctx) {
        WenyanValue left = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(0));
        WenyanValue right = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(1));
        // although the constOf do nothing here,
        // it is better to keep the code consistent
        try {
            left = WenyanValue.constOf(left).casting(WenyanValue.Type.BOOL);
            right = WenyanValue.constOf(right).casting(WenyanValue.Type.BOOL);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        return switch (ctx.op.getType()) {
            case WenyanRParser.AND -> functionEnvironment.resultStack.push(new WenyanValue(WenyanValue.Type.BOOL,
                    (boolean) left.getValue() && (boolean) right.getValue(), true));
            case WenyanRParser.OR -> functionEnvironment.resultStack.push(new WenyanValue(WenyanValue.Type.BOOL,
                    (boolean) left.getValue() || (boolean) right.getValue(), true));
            default -> throw new WenyanException("unknown operator", ctx);
        };
    }

    @Override
    public WenyanValue visitMod_math_statement(WenyanRParser.Mod_math_statementContext ctx) {
        WenyanValue left;
        if (ctx.ZHI() != null)
            left = WenyanValue.constOf(functionEnvironment.resultStack.peek());
        else
            left = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(0));
        WenyanValue right = new WenyanDataVisitor(functionEnvironment).visit(ctx.data(1));
        left = WenyanValue.constOf(left);
        right = WenyanValue.constOf(right);
        try {
            return switch (ctx.pp.getType()) {
                case WenyanRParser.PREPOSITION_RIGHT -> functionEnvironment.resultStack.push(left.mod(right));
                case WenyanRParser.PREPOSITION_LEFT -> functionEnvironment.resultStack.push(right.mod(left));
                default -> throw new WenyanException("unknown preposition", ctx);
            };
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }

    @Override
    public WenyanValue visitKey_function_call(WenyanRParser.Key_function_callContext ctx) {
        List<WenyanValue> args = new ArrayList<>();
        if (ctx.ZHI() != null) args.add(WenyanValue.constOf(functionEnvironment.resultStack.peek()));
        for (WenyanRParser.DataContext d : ctx.data())
            args.add(WenyanValue.constOf(new WenyanDataVisitor(functionEnvironment).visit(d)));
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(
                ctx.key_function().op.getText(), new WenyanValue.Type[0]);
        if (args.size() == 2) { // deal pp
            switch (ctx.pp.getFirst().getType()) {
                case WenyanRParser.PREPOSITION_RIGHT -> {}
                case WenyanRParser.PREPOSITION_LEFT -> args = args.reversed();
                default -> throw new WenyanException("unknown preposition", ctx);
            }
        }

        WenyanValue returnValue;
        try {
            returnValue = callFunction(sign, args.toArray(new WenyanValue[0]));
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        return (ctx.ZHI() != null) ? null : functionEnvironment.resultStack.push(returnValue);
    }

    @Override
    public WenyanValue visitDeclare_write_candy_statement(WenyanRParser.Declare_write_candy_statementContext ctx) {
        visit(ctx.declare_statement());
        int n;
        try {
            n = WenyanDataPhaser.parseInt(ctx.declare_statement().INT_NUM().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        ArrayList<WenyanValue> values = new ArrayList<>();
        for (int i = 0; i < n; i ++) {
            values.add(functionEnvironment.resultStack.get(functionEnvironment.resultStack.size() - n + i));
        }
        try {
            callFunction(new WenyanFunctionEnvironment.FunctionSign(
                    ctx.WRITE_KEY_FUNCTION().getText(), new WenyanValue.Type[0]),
                    values.toArray(new WenyanValue[0]));
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        return functionEnvironment.resultStack.peek();
    }

    @Override
    public WenyanValue visitFunction_define_statement(WenyanRParser.Function_define_statementContext ctx) {
        if (!ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(ctx.IDENTIFIER().size()-1).getText())) {
            throw new WenyanException("function name does not match", ctx);
        }
        ArrayList<WenyanValue.Type> argsType = new ArrayList<>();
        for (int i = 0; i < ctx.args.size(); i ++) {
            try {
                int n = WenyanDataPhaser.parseInt(ctx.args.get(i).getText());
                for (int j = 0; j < n; j++)
                    argsType.add(WenyanDataPhaser.parseType(ctx.type(i).getText()));
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage(), ctx);
            }
        }
        WenyanFunctionEnvironment.FunctionSign sign = new WenyanFunctionEnvironment.FunctionSign(
                ctx.IDENTIFIER(0).getText(), argsType.toArray(new WenyanValue.Type[0]));
        functionEnvironment.setFunction(sign, ctx);
        WenyanValue func = new WenyanValue(WenyanValue.Type.FUNCTION, sign, true);
        functionEnvironment.setVariable(ctx.IDENTIFIER(0).getText(), func);
        return functionEnvironment.resultStack.push(func);
    }

    @Override
    public WenyanValue visitFunction_pre_call(WenyanRParser.Function_pre_callContext ctx) {
        ArrayList<WenyanValue> args = new ArrayList<>();
        if (ctx.ZHI() != null)
            args.add(WenyanValue.constOf(functionEnvironment.resultStack.peek()));
        for (WenyanRParser.DataContext d : ctx.args)
            args.add(WenyanValue.constOf(new WenyanDataVisitor(functionEnvironment).visit(d)));

        WenyanFunctionEnvironment.FunctionSign sign =
                ctx.key_function() != null ?
                new WenyanFunctionEnvironment.FunctionSign(ctx.key_function().op.getText(), new WenyanValue.Type[0]) :
                (WenyanFunctionEnvironment.FunctionSign)
                        (new WenyanDataVisitor(functionEnvironment).visit(ctx.data(0)).getValue());
        WenyanValue returnValue;
        try {
            returnValue = callFunction(sign, args.toArray(new WenyanValue[0]));
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }

        return ctx.ZHI() != null ? null : functionEnvironment.resultStack.push(returnValue);
    }

    @Override
    public WenyanValue visitFunction_post_call(WenyanRParser.Function_post_callContext ctx) {
        int n;
        try {
            n = WenyanDataPhaser.parseInt(ctx.INT_NUM().getText());
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
        ArrayList<WenyanValue> args = new ArrayList<>();
        for (int i = 0; i < n; i ++) args.addFirst(WenyanValue.constOf(functionEnvironment.resultStack.pop()));

        WenyanFunctionEnvironment.FunctionSign sign =
                ctx.key_function() != null ?
                        new WenyanFunctionEnvironment.FunctionSign(ctx.key_function().op.getText(), new WenyanValue.Type[0]) :
                        (WenyanFunctionEnvironment.FunctionSign)
                                (new WenyanDataVisitor(functionEnvironment).visit(ctx.data()).getValue());
        try {
            return functionEnvironment.resultStack.push(callFunction(sign, args.toArray(new WenyanValue[0])));
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx);
        }
    }

    private WenyanValue callFunction(WenyanFunctionEnvironment.FunctionSign sign, WenyanValue[] args) throws WenyanException.WenyanThrowException {
        WenyanRParser.Function_define_statementContext func = functionEnvironment.getFunction(sign);
        // casting args
        for (int i = 0; i < sign.argTypes().length; i ++) {
            args[i] = args[i].casting(sign.argTypes()[i]);
        }
        if (func instanceof JavacallHandler) {
            return ((JavacallHandler) func).handle(args);
        } else {
            WenyanFunctionEnvironment functionEnvironment = new WenyanFunctionEnvironment(this.functionEnvironment);
            for (int i = 0; i < args.length; i++) {
                functionEnvironment.setVariable(func.id.get(i).getText(), WenyanValue.varOf(args[i]));
            }
            WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment);
            try {
                for (WenyanRParser.StatementContext statementContext : func.statement()) {
                    visitor.visit(statementContext);
                }
            } catch (WenyanControlVisitor.ReturnException e) {
                return e.value;
            }
            return null;
        }
    }
}
