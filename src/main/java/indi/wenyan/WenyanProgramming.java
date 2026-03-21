package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.interpreter_impl.MinecraftLanguageProvider;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanEntities;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Main mod class for Wenyan Programming
 */
@Mod(WenyanProgramming.MODID)
public class WenyanProgramming {
    /**
     * Mod identifier
     */
    public static final String MODID = "wenyan_programming";

    /**
     * Logger instance for the mod
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructor initializes the mod
     */
    public WenyanProgramming(IEventBus modEventBus, ModContainer modContainer) {
        LanguageManager.registerLanguageProvider(new MinecraftLanguageProvider());
        LoggerManager.registerLogger(LOGGER);
        register(modEventBus);
        WenyanConfig.register(modContainer);
    }

    private static void register(IEventBus modEventBus) {
        for (var dr : WenyanBlocks.ALL_DR)
            dr.register(modEventBus);
        WenyanItems.DR.register(modEventBus);
        WenyanEntities.DR.register(modEventBus);
        WenyanItems.CREATIVE_MODE_TABS.register(modEventBus);
        WyRegistration.register(modEventBus);
    }
}
