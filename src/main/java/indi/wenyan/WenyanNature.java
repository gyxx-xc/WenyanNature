package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.setup.CommonSetup;
import indi.wenyan.setup.Config;
import indi.wenyan.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.concurrent.Semaphore;

@Mod(WenyanNature.MODID)
public class WenyanNature {
    public static final String MODID = "wenyan_nature";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WenyanNature(IEventBus modEventBus, ModContainer modContainer) {
        CommonSetup.setup(modEventBus);
        Registration.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
