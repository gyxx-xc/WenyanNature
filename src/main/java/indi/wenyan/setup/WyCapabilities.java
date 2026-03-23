package indi.wenyan.setup;

import indi.wenyan.content.block.additional_module.builtin.*;
import indi.wenyan.content.item.ItemCodeHolder;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.IWenyanDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

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
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.HAND_RUNNER.getItems().toArray(ItemLike[]::new));
        event.registerItem(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY,
                (item, _) -> ItemCodeHolder.getCodeCapability(item),
                WenyanItems.THROW_RUNNER.getItems().toArray(ItemLike[]::new));
        registerDevice(event);
        registerItemDevice(event);
    }

    private static void registerItemDevice(@NonNull RegisterCapabilitiesEvent event) {
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(BitModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? BitModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.BIT_MODULE_BLOCK_ITEM);
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(MathModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? MathModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.MATH_MODULE_BLOCK_ITEM);
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(RandomModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? RandomModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.RANDOM_MODULE_BLOCK_ITEM);
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(Vec3ModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? Vec3ModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.VEC3_MODULE_BLOCK_ITEM);
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(CollectionModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? CollectionModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.COLLECTION_MODULE_BLOCK_ITEM);
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> simpleDevice(StringModuleBlock.PACKAGE,
                        () -> {
                            var name = item.get(DataComponents.CUSTOM_NAME);
                            return name == null ? StringModuleBlock.DEVICE_NAME : name.getString();
                        }),
                WenyanItems.STRING_MODULE_BLOCK_ITEM);
    }

    private static void registerDevice(@NonNull RegisterCapabilitiesEvent event) {
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.BIT_MODULE_BLOCK,
                        simpleDevice(BitModuleBlock.PACKAGE, () -> BitModuleBlock.DEVICE_NAME)),
                WenyanBlocks.BIT_MODULE_BLOCK.get());
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.MATH_MODULE_BLOCK,
                        simpleDevice(MathModuleBlock.PACKAGE, () -> MathModuleBlock.DEVICE_NAME)),
                WenyanBlocks.MATH_MODULE_BLOCK.get());
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.RANDOM_MODULE_BLOCK,
                        simpleDevice(RandomModuleBlock.PACKAGE, () -> RandomModuleBlock.DEVICE_NAME)),
                WenyanBlocks.RANDOM_MODULE_BLOCK.get());
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.VEC3_MODULE_BLOCK,
                        simpleDevice(Vec3ModuleBlock.PACKAGE, () -> Vec3ModuleBlock.DEVICE_NAME)),
                WenyanBlocks.VEC3_MODULE_BLOCK.get());
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.COLLECTION_MODULE_BLOCK,
                        simpleDevice(CollectionModuleBlock.PACKAGE, () -> CollectionModuleBlock.DEVICE_NAME)),
                WenyanBlocks.COLLECTION_MODULE_BLOCK.get());
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                simpleBlockDevice(WenyanBlocks.STRING_MODULE_BLOCK,
                        simpleDevice(StringModuleBlock.PACKAGE, () -> StringModuleBlock.DEVICE_NAME)),
                WenyanBlocks.STRING_MODULE_BLOCK.get());

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
                WenyanBlocks.BLOCK_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.ITEM_MODULE_ENTITY.get(),
                (be, _) -> be.getBlockDeviceCapability());
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                WenyanBlocks.COMMUNICATE_MODULE_ENTITY.get(),
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

    @Contract(pure = true)
    private static @NonNull IBlockCapabilityProvider<IWenyanBlockDevice, Void> simpleBlockDevice(Holder<Block> block, IWenyanDevice device) {
        return (_, pos, state, _, _) -> new IWenyanBlockDevice() {

            @Override
            public RawHandlerPackage getExecPackage() {
                return device.getExecPackage();
            }

            @Override
            public String getPackageName() {
                return device.getPackageName();
            }

            @Override
            public BlockState blockState() {
                return state;
            }

            @Override
            public BlockPos blockPos() {
                return pos;
            }

            @Override
            public boolean isRemoved() {
                return state.is(block);
            }
        };
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static @NonNull IWenyanDevice simpleDevice(RawHandlerPackage rawHandlerPackage, Supplier<String> name) {
        return new IWenyanDevice() {
            @Override
            public RawHandlerPackage getExecPackage() {
                return rawHandlerPackage;
            }

            @Override
            public String getPackageName() {
                return name.get();
            }
        };
    }
}
