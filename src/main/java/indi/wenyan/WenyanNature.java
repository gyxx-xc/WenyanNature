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

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(WenyanNature.MODID)
public class WenyanNature {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "wenyan_nature";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public WenyanNature(IEventBus modEventBus, ModContainer modContainer) {
        CommonSetup.setup(modEventBus);
        Registration.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
