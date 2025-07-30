package indi.wenyan.content.block.additional_module;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InteractiveAdditionalModuleEntity extends AbstractAdditionalModuleEntity {
    @Getter
    private final String packageName = "「im」";

    // interactive, inventory
    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「i」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    int dx = Math.max(-10, Math.min(10, context.args().get(0).as(WenyanInteger.TYPE).value()));
                    int dy = Math.max(-10, Math.min(10, context.args().get(1).as(WenyanInteger.TYPE).value()));
                    int dz = Math.max(-10, Math.min(10, context.args().get(2).as(WenyanInteger.TYPE).value()));
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
            .function("「e」", new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    assert level != null;
                    // TODO
                    var capability = level.getCapability(Capabilities.ItemHandler.BLOCK,
                            getBlockPos(), Direction.UP);
                    ItemHandlerHelper.insertItem(capability, new ItemStack(Items.DIAMOND), false);
                    return WenyanNull.NULL;
                }
            })
            .build();

    public InteractiveAdditionalModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.INTERACTIVE_MODULE_ENTITY.get(), pos, blockState);
    }
}
