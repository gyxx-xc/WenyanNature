package indi.wenyan.setup;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.mojang.text2speech.Narrator.LOGGER;

public class CommonSetup {
    public static void setup(IEventBus modEventBus) {
        modEventBus.addListener(CommonSetup::registerCapabilities);
        createConfigPath();
    }
    public static void createConfigPath(){
        Path configDir  = FMLPaths.CONFIGDIR.get();
        Path scriptsDir = configDir.resolve("WenyanNature").resolve("scripts"); // config/WenyanNature/scripts
        try {
            Files.createDirectories(scriptsDir);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] 无法创建脚本目录: {}", scriptsDir, e);
        }
    }
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
//                Registration.CRAFTING_ENTITY.get(), (o, direction) -> o.getRunnerItemHandler());
//        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
//                Registration.CRAFTING_ENTITY.get(), (o, direction) -> o.getItemHandler());
    }
}
