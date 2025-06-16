package indi.wenyan.content.entity;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HandRunnerEntity extends Projectile {
    public WenyanProgram program;
    public boolean hasRun = false;
    public int speed;

    public String output = "";

    public HandRunnerEntity(EntityType<HandRunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HandRunnerEntity(@NotNull Player holder, String code, int level) {
        super(Registration.HAND_RUNNER_ENTITY.get(), holder.level());
        speed = (int) Math.pow(10, level);
        program = new WenyanProgram(code, WenyanPackages.HAND_ENVIRONMENT, holder, this);

        Vec3 lookDirection = Vec3.directionFromRotation(holder.getXRot(), holder.getYRot()).normalize().scale(0.5);
        this.moveTo(holder.getEyePosition().add(lookDirection.x, -0.5, lookDirection.z));
        this.shoot(lookDirection.x, lookDirection.y+0.5, lookDirection.z, 0.1F, 10F);
        addDeltaMovement(holder.getDeltaMovement());
    }

    @Override
    public void tick() {
        if (!hasRun) {
            if (getDeltaMovement().length() < 0.01) {
                setDeltaMovement(Vec3.ZERO);
                if (!this.level().isClientSide())
                    program.run();
                hasRun = true;
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.5));
            }
        }
        if (!this.level().isClientSide() && hasRun) {
            if (program == null || !program.isRunning()) {
                discard();
                return;
            }
            program.handle();
            program.step(speed);
        }
        checkInsideBlocks();
        updateRotation();
        setPos(position().add(getDeltaMovement()));

        super.tick();
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (program != null && program.isRunning())
            program.stop();
        super.remove(reason);
    }

    @Override
    protected void onInsideBlock(@NotNull BlockState blockstate) {
        if (!blockstate.isAir()) {
            VoxelShape voxelshape = blockstate.getCollisionShape(level(), blockPosition());
            if (!voxelshape.isEmpty()) {
                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockPosition()).contains(position())) {
                        setDeltaMovement(Vec3.ZERO);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    public boolean ignoreExplosion(@NotNull Explosion explosion) {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        hasRun = true;
        super.readAdditionalSaveData(compound);
    }
}
