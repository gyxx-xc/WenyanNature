package indi.wenyan;

import com.mojang.logging.LogUtils;
import indi.wenyan.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(WenyanProgramming.MODID)
public class WenyanProgramming {
    public static final String MODID = "wenyan_programming";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WenyanProgramming(IEventBus modEventBus) {
        Registration.register(modEventBus);
    }
}
