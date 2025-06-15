package indi.wenyan.content.handler.feature_additions.packages.string_utiles;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtil_SubString_StartAndEnd
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtil_SubString_StartAndEnd implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING, WenyanType.INT, WenyanType.INT};
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        String original=args[0].toString();
        int end=Math.min(Integer.parseInt(args[2].toString()),original.length());
        int start=Integer.parseInt(args[1].toString());
        start=Math.max(0,Math.min(start,end));

        return new WenyanNativeValue(WenyanType.STRING,original.substring(start,end),false);
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
