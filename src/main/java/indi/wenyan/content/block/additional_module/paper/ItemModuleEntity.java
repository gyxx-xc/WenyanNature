package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.value.WenyanCapabilitySlot;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public class ItemModuleEntity extends AbstractModuleEntity {
    @Getter
    public final String basePackageName = WenyanSymbol.var("ItemModule");

    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("ItemModule.transfer"), request -> {
                var capability = getItemHandlerCapability();
                var from = request.args().getFirst().as(WenyanCapabilitySlot.TYPE);
                ItemStack remaining = ItemUtil.insertItemReturnRemaining(capability,
                        from.getStack(), false, null);
                from.getStack().setCount(remaining.getCount());
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("ItemModule.read"), request -> {
                var capability = getItemHandlerCapability();
                if (capability == null) {
                    throw new WenyanException.WenyanTypeException("無法取得物品處理器");
                }
                int slot = Math.clamp(request.args().getFirst().as(WenyanInteger.TYPE).value(),
                        0, capability.size() - 1);
                return new WenyanCapabilitySlot(blockPos().getCenter(), capability, slot);
            })
            .build();

    public ItemModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.ITEM_MODULE_ENTITY.get(), pos, blockState);
    }

    private ResourceHandler<ItemResource> getItemHandlerCapability() {
        var attached = AbstractFuluBlock.getConnectedDirection(getBlockState()).getOpposite();
        assert level != null;
        return level.getCapability(Capabilities.Item.BLOCK,
                blockPos().relative(attached), attached.getOpposite());
    }
}
