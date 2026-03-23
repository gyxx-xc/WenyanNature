package indi.wenyan.content.block.additional_module.paper;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.interpreter_impl.value.WenyanVec3;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.setup.definitions.WenyanBlocks;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
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
    private final String basePackageName = WenyanSymbol.ExplosionModule;

    // lighting fire heat harm
    @Getter
    private final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .handler(WenyanSymbol.ExplosionModule$lightning, _ -> {
                if (!(getLevel() instanceof ServerLevel sl))
                    throw new WenyanUnreachedException();
                EntityType.LIGHTNING_BOLT.spawn(sl, getBlockPos(), EntitySpawnReason.COMMAND);
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.ExplosionModule$explode, _ -> {
                assert level != null;
                level.explode(null,
                        getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                        3.0f, true, Level.ExplosionInteraction.BLOCK);
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.ExplosionModule$ignite, request -> {
                var offset = request.args().getFirst().as(WenyanVec3.TYPE).value(); 
                BlockPos pos = getBlockPos().offset((int) offset.x, (int) offset.y, (int) offset.z);
                if (!(getLevel() instanceof ServerLevel serverLevel)) {
                    throw new WenyanUnreachedException();
                }
                Items.FLINT_AND_STEEL.useOn(new UseOnContext(
                        serverLevel,
                        null,
                        InteractionHand.MAIN_HAND,
                        new ItemStack(Items.FLINT_AND_STEEL),
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)));
                return WenyanNull.NULL;
            })
            .handler(WenyanSymbol.ExplosionModule$fireball, request -> {
                var speed = request.args().getFirst().as(WenyanVec3.TYPE).value();
                if (!(getLevel() instanceof ServerLevel sl))
                    throw new WenyanUnreachedException();
                var e = new LargeFireball(EntityType.FIREBALL, sl);
                Projectile.spawnProjectile(e, sl, ItemStack.EMPTY);
                e.setPos(getBlockPos().offset(0, 1, 0).getCenter());
                e.setDeltaMovement(speed);
                return WenyanNull.NULL;
            })
            .build();

    public ExplosionModuleEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.EXPLOSION_MODULE_ENTITY.get(), pos, blockState);
    }
}
