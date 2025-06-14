package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.setup.CommonSetup;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.WenyanConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
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

        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> {
            return WenyanConfig.createConfigScreen(parent);
        });
    }
}
