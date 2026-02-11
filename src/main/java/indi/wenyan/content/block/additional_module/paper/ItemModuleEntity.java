package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.RawHandlerPackage;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.value.WenyanCapabilitySlot;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemModuleEntity extends AbstractModuleEntity {
    @Getter
    public final String basePackageName = WenyanSymbol.var("ItemModule");

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("ItemModule.transfer"), request -> {
                IItemHandler capability = getItemHandlerCapability();
                var from = request.args().getFirst().as(WenyanCapabilitySlot.TYPE);
                ItemStack result = ItemHandlerHelper.insertItemStacked(capability,
                        from.capabilities().getStackInSlot(from.slot()), true);
                int originAmount = from.capabilities().getStackInSlot(from.slot()).getCount();
                ItemStack extracted = from.capabilities().extractItem(from.slot(),
                        originAmount - result.getCount(), false);
                ItemHandlerHelper.insertItemStacked(capability, extracted, false);
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("ItemModule.read"), request -> {
                var capability = getItemHandlerCapability();
                if (capability == null) {
                    throw new WenyanException.WenyanTypeException("無法取得物品處理器");
                }
                int slot = Math.clamp(request.args().getFirst().as(WenyanInteger.TYPE).value(),
                        0, capability.getSlots() - 1);
                return new WenyanCapabilitySlot(blockPos().getCenter(), capability, slot);
            })
            .build();

    public ItemModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.ITEM_MODULE_ENTITY.get(), pos, blockState);
    }

    private IItemHandler getItemHandlerCapability() {
        var attached = AbstractFuluBlock.getConnectedDirection(getBlockState()).getOpposite();
        assert level != null;
        return level.getCapability(Capabilities.ItemHandler.BLOCK,
                blockPos().relative(attached), attached.getOpposite());
    }
}
