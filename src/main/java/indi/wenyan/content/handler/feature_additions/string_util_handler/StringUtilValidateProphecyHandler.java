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
 * @className StringUtilValidateProphecyHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilValidateProphecyHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING};

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String original= args.get(0).toString();
        boolean result;
        try {
            Pattern.compile(original);
            result= true;
        } catch (PatternSyntaxException e) {
            result= false;
        }
        return new WenyanNativeValue(WenyanType.BOOL,result,false);
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
