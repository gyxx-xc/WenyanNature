package indi.wenyan.content.handler.feature_additions.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilSubStringStartAndEndHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilSubStringStartAndEndHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING, WenyanType.INT, WenyanType.INT};
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandlers.getArgs(wenyan_args, ARGS_TYPE);
        String original=args[0].toString();
        int start=Integer.parseInt(args[1].toString());
        int end=Integer.parseInt(args[2].toString());
        if (start>original.length()){
            throw new WenyanException("謬：始数不可超文长");
        }else if (start<0){
            throw new WenyanException("謬：始数不可为负数");
        }

        return new WenyanNativeValue(WenyanType.STRING,original.substring(start,end),false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
