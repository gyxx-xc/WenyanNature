package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.setup.event.GetChatMsgEvent;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetMeMsgHandler
 * @Description TODO 万言感
 * @date 2025/6/14 20:29
 */
public class GetLastMsgHandler implements JavacallHandler {
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        String lastMsg=GetChatMsgEvent.getLastMessage();
        return new WenyanNativeValue(WenyanType.STRING,lastMsg,false);
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
