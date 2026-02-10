package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.setup.Registration;
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
        Registration.register(modEventBus);
    }
}
