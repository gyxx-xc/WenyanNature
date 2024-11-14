package indi.wenyan.setup.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static indi.wenyan.WenyanNature.LOGGER;
import static indi.wenyan.WenyanNature.MODID;

@EventBusSubscriber(modid = MODID)
public class ServerEvent {
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("道曰：「「问天地好在」」");
    }
}
