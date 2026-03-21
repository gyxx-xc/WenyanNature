package indi.wenyan.client;

import indi.wenyan.WenyanProgramming;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = WenyanProgramming.MODID, dist = Dist.CLIENT)
public class WenyanProgrammingClient {
    public WenyanProgrammingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (_, parent) -> new ConfigurationScreen(container, parent));
    }
}
