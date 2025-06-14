package indi.wenyan.content.handler.feature_additions.packages.string_utiles;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtil_ToLowerCase
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtil_ToLowerCase implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING};
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        String original=(String)args[0];

        return new WenyanNativeValue(WenyanType.STRING,original.toLowerCase(),false);
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
