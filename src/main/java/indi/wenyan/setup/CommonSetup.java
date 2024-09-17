package indi.wenyan.setup;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CommonSetup {

    public static void setup(IEventBus modEventBus) {
        modEventBus.addListener(CommonSetup::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                Registration.CRAFTING_ENTITY.get(), (o, direction) -> o.getItemHandler());
    }

}
