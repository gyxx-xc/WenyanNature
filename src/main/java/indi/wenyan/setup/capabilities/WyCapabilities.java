package indi.wenyan.setup.capabilities;

import indi.wenyan.content.block.additional_module.builtin.*;
import indi.wenyan.content.item.ItemCodeHolder;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.NonNull;

import static indi.wenyan.WenyanProgramming.MODID;

@EventBusSubscriber(modid = MODID)
public enum WyCapabilities {
    ;

    /**
     * Registers capabilities for mod blocks and entities
     */
    @SubscribeEvent
    public static void registerCapabilities(@NonNull RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.PEDESTAL_ENTITY.get(),
                (be, _) -> be.getItemHandler());
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.WRITING_BLOCK_ENTITY.get(),
                (be, _) -> be.getItemHandler());
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                WenyanBlocks.LOGIC_FURNACE_ENTITY.get(),
                (be, direction) -> direction == Direction.DOWN ?
                        be.getOutput() : be.getInput());
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.HAND_RUNNER.getItems().toArray(ItemLike[]::new));
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.THROW_RUNNER.getItems().toArray(ItemLike[]::new));

        DeviceCapabilityRegisterer registerer = new DeviceCapabilityRegisterer(event);
        registerItemDevice(registerer);
        registerDevice(registerer);
    }

    private static void registerItemDevice(DeviceCapabilityRegisterer registerer) {
        registerer.registerToItem(_ -> BitModuleBlock.PACKAGE, BitModuleBlock.DEVICE_NAME, WenyanItems.BIT_MODULE_BLOCK_ITEM);
        registerer.registerToItem(_ -> MathModuleBlock.PACKAGE, MathModuleBlock.DEVICE_NAME, WenyanItems.MATH_MODULE_BLOCK_ITEM);
        registerer.registerToItem(_ -> RandomModuleBlock.PACKAGE, RandomModuleBlock.DEVICE_NAME, WenyanItems.RANDOM_MODULE_BLOCK_ITEM);
        registerer.registerToItem(_ -> Vec3ModuleBlock.PACKAGE, Vec3ModuleBlock.DEVICE_NAME, WenyanItems.VEC3_MODULE_BLOCK_ITEM);
        registerer.registerToItem(_ -> CollectionModuleBlock.PACKAGE, CollectionModuleBlock.DEVICE_NAME, WenyanItems.COLLECTION_MODULE_BLOCK_ITEM);
        registerer.registerToItem(_ -> StringModuleBlock.PACKAGE, StringModuleBlock.DEVICE_NAME, WenyanItems.STRING_MODULE_BLOCK_ITEM);
    }

    private static void registerDevice(DeviceCapabilityRegisterer registerer) {
        registerer.registerToBlock((_, _) -> BitModuleBlock.PACKAGE, BitModuleBlock.DEVICE_NAME, WenyanBlocks.BIT_MODULE_BLOCK.get());
        registerer.registerToBlock((_, _) -> MathModuleBlock.PACKAGE, MathModuleBlock.DEVICE_NAME, WenyanBlocks.MATH_MODULE_BLOCK.get());
        registerer.registerToBlock((_, _) -> RandomModuleBlock.PACKAGE, RandomModuleBlock.DEVICE_NAME, WenyanBlocks.RANDOM_MODULE_BLOCK.get());
        registerer.registerToBlock((_, _) -> Vec3ModuleBlock.PACKAGE, Vec3ModuleBlock.DEVICE_NAME, WenyanBlocks.VEC3_MODULE_BLOCK.get());
        registerer.registerToBlock((_, _) -> CollectionModuleBlock.PACKAGE, CollectionModuleBlock.DEVICE_NAME, WenyanBlocks.COLLECTION_MODULE_BLOCK.get());
        registerer.registerToBlock((_, _) -> StringModuleBlock.PACKAGE, StringModuleBlock.DEVICE_NAME, WenyanBlocks.STRING_MODULE_BLOCK.get());

        registerer.registerToModule(WenyanBlocks.BLOCKING_QUEUE_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.EXPLOSION_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.INFORMATION_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.BLOCK_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.ITEM_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.ENTITY_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.SCREEN_MODULE_BLOCK_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.LOCK_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.FORMATION_CORE_MODULE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.PISTON_MODULE_ENTITY.get());

        registerer.registerToModule(WenyanBlocks.LOGIC_FURNACE_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.POWER_BLOCK_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.CREATIVE_POWER_BLOCK_ENTITY.get());
        registerer.registerToModule(WenyanBlocks.CRAFTING_BLOCK_ENTITY.get());
    }
}
