package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.setup.CommonSetup;
import indi.wenyan.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.neoforged.neoforgespi.ILaunchContext.LOGGER;

@Mod(WenyanNature.MODID)
public class WenyanNature {
    public static final String MODID = "wenyan_nature";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WenyanNature(IEventBus modEventBus) {
        CommonSetup.setup(modEventBus);
        Registration.register(modEventBus);
    }
}
