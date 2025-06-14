package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.setup.event.GetChatMsgEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetMeMsgHandler
 * @Description TODO 感言
 * @date 2025/6/14 20:29
 */
public class GetMeMsgHandler implements JavacallHandler {
    private Player player;
    public GetMeMsgHandler(Player player) {
        this.player = player;
    }
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        CompoundTag modData = player.getPersistentData().getCompound("wenyan_data");
        String playerLastMessage = GetChatMsgEvent.getPlayerLastMessage(player);
        //检测是否包含过滤规则
        if (modData.contains("MyChatHideFilter")){
            //如果包含则仅输出包含信息
            String prefix = modData.getString("MyChatHideFilter");
            if (playerLastMessage.startsWith(prefix)) {
                playerLastMessage = playerLastMessage.substring(prefix.length());
            }
            return new WenyanNativeValue(WenyanType.STRING,playerLastMessage,false);
        }else {
            return new WenyanNativeValue(WenyanType.STRING,playerLastMessage,false);
        }
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
