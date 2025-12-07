package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.warper.WenyanVec3;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanSymbol;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExplosionModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = WenyanSymbol.var("ExplosionModule");

    // lighting fire heat harm
    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function(WenyanSymbol.var("ExplosionModule.lightning"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    assert getLevel() != null;
                    Entity e = EntityType.LIGHTNING_BOLT.create(getLevel());
                    if (e == null) {
                        return WenyanNull.NULL;
                    }
                    e.moveTo(getBlockPos().getCenter());
                    getLevel().addFreshEntity(e);
                    return WenyanNull.NULL;
                }
            })
            .function(WenyanSymbol.var("ExplosionModule.explode"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) {
                    assert level != null;
                    level.explode(null,
                            getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                            3.0f, true, Level.ExplosionInteraction.BLOCK);
                    return WenyanNull.NULL;
                }
            })
            .function(WenyanSymbol.var("ExplosionModule.ignite"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    var offset = context.args().getFirst().as(WenyanVec3.TYPE).value();
                    BlockPos pos = getBlockPos().offset((int) offset.x, (int) offset.y, (int) offset.z);
                    if (!(getLevel() instanceof ServerLevel serverLevel)) {
                        throw new WenyanException("unreached");
                    }
                    Items.FLINT_AND_STEEL.useOn(new UseOnContext(
                            serverLevel,
                            null,
                            InteractionHand.MAIN_HAND,
                            new ItemStack(Items.FLINT_AND_STEEL),
                            new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
                    return WenyanNull.NULL;
                }
            })
            .function(WenyanSymbol.var("ExplosionModule.fireball"), new ThisCallHandler() {
                @Override
                public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
                    var speed = context.args().getFirst().as(WenyanVec3.TYPE).value();
                    assert getLevel() != null;
                    Entity e = EntityType.FIREBALL.create(getLevel());
                    if (e == null) {
                        return WenyanNull.NULL;
                    }
                    e.moveTo(getBlockPos().offset(0, 1, 0).getCenter());
                    e.setDeltaMovement(speed);
                    getLevel().addFreshEntity(e);
                    return WenyanNull.NULL;
                }
            })
            .build();

    public ExplosionModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.EXPLOSION_MODULE_ENTITY.get(), pos, blockState);
    }
}
