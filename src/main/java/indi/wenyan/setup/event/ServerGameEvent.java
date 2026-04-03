package indi.wenyan.setup.event;

import indi.wenyan.interpreter_impl.FileLoader;
import indi.wenyan.setup.definitions.WyRegistration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static indi.wenyan.WenyanProgramming.LOGGER;
import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Server-side event handler
 */
@EventBusSubscriber(modid = MODID)
public enum ServerGameEvent {;
    /**
     * Called when the server is starting
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("道曰：「「问天地好在」」");
    }

    /**
     * Registers commands
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(FileLoader.FILE_COMMAND);
    }

    @SubscribeEvent
    public static void syncRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(WyRegistration.ANSWERING_RECIPE_TYPE.get());
    }
}
