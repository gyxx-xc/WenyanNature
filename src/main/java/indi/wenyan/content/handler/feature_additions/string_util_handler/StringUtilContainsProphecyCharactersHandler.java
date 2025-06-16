package indi.wenyan.content.handler.feature_additions.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilContainsProphecyCharactersHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilContainsProphecyCharactersHandler implements JavacallHandler {
    private static final String PROPHECY_CHARACTERS = ".[]()*+?{}|^$\\";
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING};


    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String original=args.getFirst().toString();
        boolean result=false;
        for (char c : original.toCharArray()) {
            if (PROPHECY_CHARACTERS.indexOf(c) != -1) {
                result=true;
                break;
            }
        }
        return new WenyanNativeValue(WenyanType.BOOL,result,false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
