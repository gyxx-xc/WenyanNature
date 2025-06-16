package indi.wenyan.content.handler.feature_additions.string_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.setup.event.GetChatMessageEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetMyMessageHandler
 * @Description TODO 感言
 * @date 2025/6/14 20:29
 */
public class GetMyMessageHandler implements JavacallHandler {
    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        Player player=context.holder();
        CompoundTag modData = player.getPersistentData().getCompound("wenyan_data");
        String playerLastMessage = GetChatMessageEvent.getPlayerLastMessage(player);
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
