package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.warper.WenyanCapabilitySlot;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InventoryModuleEntity extends AbstractModuleEntity {
    @Getter
    public final String BasePackageName = "「仓」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「移」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    var capability = getItemHandlerCapability();
                    var from = context.args().getFirst().as(WenyanCapabilitySlot.TYPE);
                    var result = ItemHandlerHelper.insertItemStacked(capability,
                            from.capabilities().getStackInSlot(from.slot()), true);
                    int originAmount = from.capabilities().getStackInSlot(from.slot()).getCount();
                    var extracted = from.capabilities().extractItem(from.slot(),
                            originAmount - result.getCount(), false);
                    ItemHandlerHelper.insertItemStacked(capability, extracted, false);
                    return WenyanNull.NULL;
                }
            })
            .function("「讀」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    var capability = getItemHandlerCapability();
                    if (capability == null) {
                        throw new WenyanException.WenyanTypeException("無法取得物品處理器");
                    }
                    int slot = Math.clamp(context.args().getFirst().as(WenyanInteger.TYPE).value(),
                            0, capability.getSlots() - 1);
                    return new WenyanCapabilitySlot(capability, slot);
                }
            })
            .build();

    public InventoryModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INVENTORY_MODULE_ENTITY.get(), pos, blockState);
    }

    private IItemHandler getItemHandlerCapability() {
        var attached = InteractModuleBlock.getConnectedDirection(getBlockState()).getOpposite();
        assert level != null;
        return level.getCapability(Capabilities.ItemHandler.BLOCK,
                getBlockPos().relative(attached), attached.getOpposite());
    }
}
