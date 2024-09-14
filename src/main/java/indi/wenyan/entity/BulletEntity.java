package indi.wenyan.entity;

import indi.wenyan.setup.Registration;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BulletEntity extends Projectile {
    private int aliveTime;
    private int tickCount = 0;

    public BulletEntity(EntityType<BulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BulletEntity(Level level, Vec3 pos, Vec3 direction, double speed, int aliveTime) {
        super(Registration.BULLET_ENTITY.get(), level);
        this.aliveTime = aliveTime;
        this.moveTo(pos);
        this.shoot(direction.x, direction.y, direction.z, (float) speed, 0);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide())
            if (this.tickCount++ > aliveTime)
                this.remove(RemovalReason.KILLED);
        setPos(position().add(getDeltaMovement()));
        super.tick();
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

}
