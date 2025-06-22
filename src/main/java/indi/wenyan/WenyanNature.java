package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.setup.CommonSetup;
import indi.wenyan.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(WenyanNature.MODID)
public class WenyanNature {
    public static final String MODID = "wenyan_nature";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WenyanNature(IEventBus modEventBus) {
        CommonSetup.setup(modEventBus);
        Registration.register(modEventBus);
    }
}
