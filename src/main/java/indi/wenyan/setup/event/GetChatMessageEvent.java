package indi.wenyan.setup.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static indi.wenyan.WenyanNature.MODID;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className GetChatMessageEvent
 * @Description TODO 消息事件监听
 * @date 2025/6/14 22:06
 */
@EventBusSubscriber(modid = MODID)
public class GetChatMessageEvent {
    private static String lastMsg="";//所有信息的最后一条
    private static final Map<UUID, String> lastMessages = new HashMap<>();//每个玩家的最后一条信息
    //  服务端接收后
    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event) {
        CompoundTag persistentData =event.getPlayer().getPersistentData();
        CompoundTag modData = persistentData.getCompound("wenyan_data");
        if (modData.contains("MyChatHideFilter")){
            if (event.getRawText().startsWith(modData.getString("MyChatHideFilter"))) {
                lastMessages.put(event.getPlayer().getUUID(),event.getRawText());
                event.setCanceled(true);
            }else {
                lastMsg=event.getRawText();
            }
        }else {
            lastMessages.put(event.getPlayer().getUUID(),event.getRawText());
            lastMsg=event.getRawText();
        }
    }
    public static String getLastMessage() {
        return lastMsg;
    }
    public static String getPlayerLastMessage(Player player) {
        if (lastMessages.get(player.getUUID()) == null) {
            return "";
        }
        return lastMessages.get(player.getUUID());
    }
}
