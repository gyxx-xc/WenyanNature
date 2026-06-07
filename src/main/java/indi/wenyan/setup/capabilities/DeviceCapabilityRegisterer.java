package indi.wenyan.setup.capabilities;

import indi.wenyan.content.block.IWenyanDevice;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.setup.definitions.WyRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.BiFunction;
import java.util.function.Function;

public class DeviceCapabilityRegisterer {
    private final RegisterCapabilitiesEvent event;

    public DeviceCapabilityRegisterer(RegisterCapabilitiesEvent event) {
        this.event = event;
    }

    public void registerToItem(Function<ItemStack, RawHandlerPackage> aPackage,
                               String deviceName, DeferredItem<? extends Item> moduleItem) {
        event.registerItem(WyRegistration.WENYAN_ITEM_DEVICE_CAPABILITY,
                (item, _) -> new IWenyanDevice() {
                    @Override
                    public RawHandlerPackage getExecPackage() {
                        return aPackage.apply(item);
                    }

                    @Override
                    public String getPackageName() {
                        var name = item.get(DataComponents.CUSTOM_NAME);
                        return name == null ? deviceName : name.getString();
                    }
                },
                moduleItem);
    }

    public void registerToBlock(BiFunction<BlockPos, BlockState, RawHandlerPackage> packageSupplier,
                                String deviceName, Block... bitModuleBlock) {
        event.registerBlock(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                (_, pos, state, _, _) -> new IWenyanBlockDevice() {

                    @Override
                    public RawHandlerPackage getExecPackage() {
                        return packageSupplier.apply(pos, state);
                    }

                    @Override
                    public String getPackageName() {
                        return deviceName;
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
                        return false;
                    }
                },
                bitModuleBlock);
    }

    public void registerToModule(BlockEntityType<? extends AbstractModuleEntity> blockingQueueModuleEntityBlockEntityType) {
        event.registerBlockEntity(
                WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY,
                blockingQueueModuleEntityBlockEntityType,
                (be, _) -> be.getBlockDeviceCapability());
    }
}
