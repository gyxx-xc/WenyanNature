package indi.wenyan.content.block.additional_module.paper;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.WenyanSymbol;
import indi.wenyan.setup.definitions.WenyanBlocks;
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
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.var("ExplosionModule.lightning"), request -> {
                assert getLevel() != null;
                Entity e = EntityType.LIGHTNING_BOLT.create(getLevel());
                if (e == null) {
                    return WenyanNull.NULL;
                }
                e.moveTo(blockPos().getCenter());
                getLevel().addFreshEntity(e);
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("ExplosionModule.explode"), request -> {
                assert level != null;
                level.explode(null,
                        blockPos().getX() + 0.5, blockPos().getY() + 0.5, blockPos().getZ() + 0.5,
                        3.0f, true, Level.ExplosionInteraction.BLOCK);
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("ExplosionModule.ignite"), request -> {
                var offset = request.args().getFirst().as(WenyanVec3.TYPE).value(); 
                BlockPos pos = blockPos().offset((int) offset.x, (int) offset.y, (int) offset.z);
                if (!(getLevel() instanceof ServerLevel serverLevel)) {
                    throw new WenyanException.WenyanUnreachedException();
                }
                Items.FLINT_AND_STEEL.useOn(new UseOnContext(
                        serverLevel,
                        null,
                        InteractionHand.MAIN_HAND,
                        new ItemStack(Items.FLINT_AND_STEEL),
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.var("ExplosionModule.fireball"), request -> {
                var speed = request.args().getFirst().as(WenyanVec3.TYPE).value();
                assert getLevel() != null;
                Entity e = EntityType.FIREBALL.create(getLevel());
                if (e == null) {
                    return WenyanNull.NULL;
                }
                e.moveTo(blockPos().offset(0, 1, 0).getCenter());
                e.setDeltaMovement(speed);
                getLevel().addFreshEntity(e);
                return WenyanNull.NULL;
            })
            .build();

    public ExplosionModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.EXPLOSION_MODULE_ENTITY.get(), pos, blockState);
    }
}
