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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InteractModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「相」";

    // interactive, inventory
    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「觸」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    int dx = Math.clamp(context.args().get(0).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    int dy = Math.clamp(context.args().get(1).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    int dz = Math.clamp(context.args().get(2).as(WenyanInteger.TYPE).value(),
                            -10, 10);
                    BlockPos blockPos = getBlockPos().offset(dx, dy, dz);
                    assert level != null;
                    level.getProfiler().push("explosion_blocks");
                    level.getBlockState(blockPos).onExplosionHit(level, blockPos,
                            new Explosion(level, null, blockPos.getX(), blockPos.getY(),
                                    blockPos.getZ(),
                                    1.0f, false, Explosion.BlockInteraction.TRIGGER_BLOCK), (a1, a2) -> {
                            });
                    level.getProfiler().pop();
                    return WenyanNull.NULL;
                }
            })
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
            .function("「取」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    var capability = getItemHandlerCapability();
                    if (capability == null) {
                        throw new WenyanException.WenyanTypeException("無法取得物品處理器");
                    }
                    var item = capability.extractItem(0, 0, false);
                    // TODO: item
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

    public InteractModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INTERACT_MODULE_ENTITY.get(), pos, blockState);
    }

    private IItemHandler getItemHandlerCapability() {
        var attached = InteractModuleBlock.getConnectedDirection(getBlockState()).getOpposite();
        assert level != null;
        return level.getCapability(Capabilities.ItemHandler.BLOCK,
                getBlockPos().relative(attached), attached.getOpposite());
    }
}
