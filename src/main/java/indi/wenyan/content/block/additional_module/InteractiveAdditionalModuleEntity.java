package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.warper.WenyanItemstack;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InteractiveAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「相」";

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
            .function("「儲」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    assert level != null;
                    var attached =
                            InteractiveAdditionalModuleBlock.getConnectedDirection(getBlockState());
                    var capability = level.getCapability(Capabilities.ItemHandler.BLOCK,
                            getBlockPos().relative(attached), attached.getOpposite());
                    // TODO: item
                    ItemHandlerHelper.insertItem(capability, new ItemStack(Items.DIAMOND), false);
                    return WenyanNull.NULL;
                }
            })
            .function("「取」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    assert level != null;
                    var attached =
                            InteractiveAdditionalModuleBlock.getConnectedDirection(getBlockState());
                    var capability = level.getCapability(Capabilities.ItemHandler.BLOCK,
                            getBlockPos().relative(attached), attached.getOpposite());
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
                    assert level != null;
                    var attached =
                            InteractiveAdditionalModuleBlock.getConnectedDirection(getBlockState());
                    var capability = level.getCapability(Capabilities.ItemHandler.BLOCK,
                            getBlockPos().relative(attached), attached.getOpposite());
                    if (capability == null) {
                        throw new WenyanException.WenyanTypeException("無法取得物品處理器");
                    }
                    int slot = Math.clamp(context.args().getFirst().as(WenyanInteger.TYPE).value(),
                            0, capability.getSlots() - 1);
                    var item = capability.getStackInSlot(slot);
                    return new WenyanItemstack(item);
                }
            })
            .build();

    public InteractiveAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INTERACTIVE_MODULE_ENTITY.get(), pos, blockState);
    }
}
