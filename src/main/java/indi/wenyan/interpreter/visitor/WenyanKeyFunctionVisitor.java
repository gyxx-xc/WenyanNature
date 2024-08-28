package indi.wenyan.interpreter.visitor;

import indi.wenyan.WenyanRBaseVisitor;
import indi.wenyan.WenyanRParser;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;

public class WenyanKeyFunctionVisitor extends
        WenyanRBaseVisitor<WenyanKeyFunctionVisitor.WenyanFunction> {
    @Override
    public WenyanFunction visitKey_function(WenyanRParser.Key_functionContext ctx) {
        return switch (ctx.op.getType()) {
            case WenyanRParser.ADD -> argsCheck(2, args -> args[0].add(args[1]));
            case WenyanRParser.SUB -> argsCheck(2, args -> args[0].sub(args[1]));
            case WenyanRParser.MUL -> argsCheck(2, args -> args[0].mul(args[1]));
            case WenyanRParser.DIV -> argsCheck(2, args -> args[0].div(args[1]));
            case WenyanRParser.UNARY_OP -> argsCheck(1, args -> args[0].not());
            case WenyanRParser.ARRAY_ADD_OP -> arrayAddFunction();
            case WenyanRParser.ARRAY_COMBINE_OP -> arrayCombineFunction();
            case WenyanRParser.WRITE_KEY_FUNCTION -> writeKeyFunction();
            default -> throw new IllegalStateException("Unexpected value: " + ctx.op.getType());
        };
    }

    @FunctionalInterface
    public interface WenyanFunction {
        WenyanValue apply(WenyanValue[] args) throws WenyanException.WenyanThrowException;
    }

    private static WenyanFunction argsCheck(int n, WenyanFunction function) {
        return args -> {
            if (args.length != n)
                throw new RuntimeException("number of arguments does not match");
            return function.apply(args);
        };
    }

    public static WenyanFunction writeKeyFunction() {
        return args -> {
            String result = "";
            for (WenyanValue arg : args) {
                result += (result.isEmpty()?"":" ") + arg.toString();
            }
            System.out.println(result);
            return new WenyanValue(WenyanValue.Type.STRING, result, true);
        };
    }

    public static WenyanFunction arrayAddFunction() {
        return args -> {
            if (args.length <= 1)
                throw new WenyanException.WenyanVarException("number of arguments does not match");
            for (int i = 1; i < args.length; i++) {
                args[0].append(args[i]);
            }
            return args[0];
        };
    }

    public static WenyanFunction arrayCombineFunction() {
        return args -> {
            if (args.length <= 1)
                throw new WenyanException.WenyanVarException("number of arguments does not match");
            for (int i = 1; i < args.length; i++) {
                args[0].add(args[i]);
            }
            return args[0];
        };
    }
}
