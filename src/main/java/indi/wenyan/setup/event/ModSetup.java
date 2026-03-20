package indi.wenyan.setup.event;

import indi.wenyan.content.item.ItemCodeHolder;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.judou.exec_interface.IWenyanDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.utils.WenyanValues;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import static indi.wenyan.WenyanProgramming.MODID;

/**
 * Common mod setup handling events
 */
@EventBusSubscriber(modid = MODID)
public enum ModSetup {
    ;

    /**
     * Registers capabilities for mod blocks and entities
     */
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.PEDESTAL_ENTITY.get(),
                (be, _) -> be.getItemHandler());
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.WRITING_BLOCK_ENTITY.get(),
                (be, _) -> be.getItemHandler());
        registerDevice(event);
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.HAND_RUNNER.getItems().toArray(ItemLike[]::new));
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.THROW_RUNNER.getItems().toArray(ItemLike[]::new));

        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (_, _) -> new IWenyanDevice() {
                    @Override
                    public RawHandlerPackage getExecPackage() {
                        return HandlerPackageBuilder.create()
                                .handler("「乙」", (HandlerPackageBuilder.HandlerReturnFunction)
                                        (_, _) -> WenyanValues.of(100))
                                .build();
                    }

                    @Override
                    public String getPackageName() {
                        return "「甲」";
                    }
                },
                WenyanItems.PRINT_INVENTORY_MODULE.get());
    }

    private static void registerDevice(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.EXPLOSION_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.INFORMATION_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.MATH_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.BIT_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.BLOCK_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.RANDOM_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.ITEM_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.VEC3_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.COMMUNICATE_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.COLLECTION_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.STRING_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.ENTITY_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.LOCK_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.PISTON_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
    }

}
