package indi.wenyan.setup.event;

import indi.wenyan.setup.Registration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Common mod setup handling events
 */
@EventBusSubscriber(modid = MODID)
public enum ModSetup {;
    /**
     * Registers capabilities for mod blocks and entities
     */
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                Registration.PEDESTAL_ENTITY.get(),
                (be, side) -> be.getItemHandler());
    }

}
