package indi.wenyan.content.handler.feature_additions.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilSplitHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilSplitHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING,WenyanType.STRING};

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String original=args.get(0).toString();
        String toSplit=args.get(1).toString();

        List<String> stringList= List.of(original.split(toSplit));
        WenyanArrayObject list=new WenyanArrayObject();
        for (String string : stringList) {
            list.add(new WenyanNativeValue(WenyanType.STRING,string,false));
        }
        return new WenyanNativeValue(WenyanType.LIST,list,false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
