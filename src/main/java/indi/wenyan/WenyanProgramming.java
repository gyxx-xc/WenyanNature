package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.interpreter_impl.MinecraftLanguageProvider;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.setup.definitions.Registration;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
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
    public WenyanProgramming(IEventBus modEventBus, @SuppressWarnings("unused") ModContainer modContainer) {
        LanguageManager.registerLanguageProvider(new MinecraftLanguageProvider());
        LoggerManager.registerLogger(LOGGER);
        register(modEventBus);
    }

    private static void register(IEventBus modEventBus) {
        WenyanBlocks.DR.register(modEventBus);
        WenyanBlocks.DR_ENTITY.register(modEventBus);
        WenyanItems.DR.register(modEventBus);
        Registration.register(modEventBus);
    }
}
