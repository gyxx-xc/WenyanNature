package indi.wenyan.content.handler.feature_additions.packages.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

import java.util.regex.PatternSyntaxException;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilMatchesHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilMatchesHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING, WenyanType.STRING};
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        String original=args[0].toString();
        String pattern=args[1].toString();
        boolean result;
        try {
            result=original.matches(pattern);
        }catch (PatternSyntaxException e){
            throw new WenyanException(String.format("谶文有凶：位：%d  咎：%s",
                    e.getIndex(), e.getDescription()));
        }
        return new WenyanNativeValue(WenyanType.BOOL,result,false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
