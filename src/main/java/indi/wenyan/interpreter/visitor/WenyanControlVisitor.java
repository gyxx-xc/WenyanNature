package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.antlr.WenyanRBaseVisitor;
import indi.wenyan.interpreter.antlr.WenyanRParser;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.network.chat.Component;

// this class is for
// flush_statement
// if_statement
// for_statement
// return_statement
// BREAK
public class WenyanControlVisitor extends WenyanVisitor{
    public WenyanControlVisitor(WenyanFunctionEnvironment functionEnvironment, WenyanControl control) {
        super(functionEnvironment, control);
    }

    @Override
    public WenyanValue visitFlush_statement(WenyanRParser.Flush_statementContext ctx) {
        functionEnvironment.resultStack.empty();
        return null;
    }

    @Override
    public WenyanValue visitIf_statement(WenyanRParser.If_statementContext ctx) {
        if (new IfExprVisitor(functionEnvironment, control).visit(ctx.if_expression())) {
            WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
            for (WenyanRParser.StatementContext statementContext : ctx.if_) {
                visitor.visit(statementContext);
            }
        } else if (!ctx.else_.isEmpty()) {
            WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
            for (WenyanRParser.StatementContext statementContext : ctx.else_) {
                visitor.visit(statementContext);
            }
        }
        return null;
    }

    @Override
    public WenyanValue visitFor_arr_statement(WenyanRParser.For_arr_statementContext ctx) {
        WenyanValue value = new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data());
        try {
            value = WenyanValue.constOf(value).casting(WenyanValue.Type.LIST);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx.data());
        }
        WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
        for (WenyanValue item : (WenyanValue.WenyanValueArray) value.getValue()) {
            functionEnvironment.setVariable(ctx.IDENTIFIER().getText(), item);
            try {
                for (WenyanRParser.StatementContext statementContext : ctx.statement()) {
                    visitor.visit(statementContext);
                }
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) { // i.e. continue
            }
        }
        return null;
    }

    @Override
    public WenyanValue visitFor_enum_statement(WenyanRParser.For_enum_statementContext ctx) {
        WenyanValue value = new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data());
        try {
            value = WenyanValue.constOf(value).casting(WenyanValue.Type.INT);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage(), ctx.data());
        }
        int count = (int) value.getValue();
        WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
        for (int i = 0; i < count; i++) {
            try {
                for (WenyanRParser.StatementContext statementContext : ctx.statement()) {
                    visitor.visit(statementContext);
                }
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) { // i.e. continue
            }
        }
        return null;
    }

    @Override
    public WenyanValue visitFor_while_statement(WenyanRParser.For_while_statementContext ctx) {
        WenyanMainVisitor visitor = new WenyanMainVisitor(functionEnvironment, control);
        while (true) {
            try {
                for (WenyanRParser.StatementContext statementContext : ctx.statement()) {
                    visitor.visit(statementContext);
                }
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) { // i.e. continue
            }
        }
        return null;
    }

    @Override
    public WenyanValue visitBreak_(WenyanRParser.Break_Context ctx) {
        throw new BreakException();
    }

    @Override
    public WenyanValue visitContinue_(WenyanRParser.Continue_Context ctx) {
        throw new ContinueException();
    }

    @Override
    public WenyanValue visitReturn_data_statement(WenyanRParser.Return_data_statementContext ctx) {
        throw new ReturnException(new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data()));
    }

    @Override
    public WenyanValue visitReturn_last_statement(WenyanRParser.Return_last_statementContext ctx) {
        throw new ReturnException(functionEnvironment.resultStack.peek());
    }

    @Override
    public WenyanValue visitReturn_void_statement(WenyanRParser.Return_void_statementContext ctx) {
        throw new ReturnException(null);
    }

    private static class IfExprVisitor extends WenyanRBaseVisitor<Boolean> {
        protected final WenyanFunctionEnvironment functionEnvironment;
        protected final WenyanControl control;

        public IfExprVisitor(WenyanFunctionEnvironment functionEnvironment, WenyanControl control) {
            this.functionEnvironment = functionEnvironment;
            this.control = control;
        }

        @Override
        public Boolean visitIf_data(WenyanRParser.If_dataContext ctx) {
            WenyanValue value = new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data());
            try {
                value = WenyanValue.constOf(value).casting(WenyanValue.Type.BOOL);
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage(), ctx.data());
            }
            return (Boolean) value.getValue();
        }

        @Override
        public Boolean visitIf_logic(WenyanRParser.If_logicContext ctx) {
            control.wait_tick();
            WenyanValue left = new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data(0));
            WenyanValue right = new WenyanDataVisitor(functionEnvironment, control).visit(ctx.data(1));
            left = WenyanValue.constOf(left);
            right = WenyanValue.constOf(right);
            try {
                return switch (ctx.if_logic_op().op.getType()) {
                    case WenyanRParser.EQ -> left.equals(right);
                    case WenyanRParser.NEQ -> !left.equals(right);
                    case WenyanRParser.GT -> left.compareTo(right) > 0;
                    case WenyanRParser.GTE -> left.compareTo(right) >= 0;
                    case WenyanRParser.LT -> left.compareTo(right) < 0;
                    case WenyanRParser.LTE -> left.compareTo(right) <= 0;
                    default -> throw new WenyanException(Component.translatable("error.wenyan_nature.unknown_operator").getString(), ctx);
                };
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage(), ctx);
            }
        }
    }

    public static class BreakException extends RuntimeException {
        public BreakException() {
            super("break");
        }
    }

    public static class ContinueException extends RuntimeException {
        public ContinueException() {
            super("continue");
        }
    }

    public static class ReturnException extends RuntimeException {
        public final WenyanValue value;

        public ReturnException(WenyanValue value) {
            super("return");
            this.value = value;
        }
    }
}
