package indi.wenyan.entity;

import indi.wenyan.setup.Registration;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BulletEntity extends Projectile {
    private int aliveTime;
    private int tickCount;

    public BulletEntity(EntityType<BulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BulletEntity(Level level, Vec3 pos, Vec3 direction, double speed, int aliveTime) {
        super(Registration.BULLET_ENTITY.get(), level);
        tickCount = 0;
        this.aliveTime = aliveTime;
        this.moveTo(pos);
        this.shoot(direction.x, direction.y, direction.z, (float) speed, 0);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide())
            if (this.tickCount++ > aliveTime)
                this.discard();
        checkInsideBlocks();
        updateRotation();
        setPos(position().add(getDeltaMovement()));
        super.tick();
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        if (state.isCollisionShapeFullBlock(level(), blockPosition()))
            this.discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }
}
