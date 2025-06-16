package indi.wenyan.content.handler.feature_additions.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilDiagnoseProphecyHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilDiagnoseProphecyHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING};


    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String original=args.get(0).toString();
        String result="无咎";
        try {
            Pattern.compile(original);
        } catch (PatternSyntaxException e) {
            result= String.format("谶文有凶：位：%d  咎：%s",
                    e.getIndex(), e.getDescription());
        }
        return new WenyanNativeValue(WenyanType.STRING,result,false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
