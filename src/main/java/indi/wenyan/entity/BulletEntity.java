package indi.wenyan.entity;

import indi.wenyan.setup.Registration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BulletEntity extends AbstractArrow {
    private int aliveTime;
    private int tickCount = 0;

    public BulletEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
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
                this.remove(RemovalReason.DISCARDED);
        super.tick();
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {return new ItemStack(Items.ACACIA_BUTTON);}

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isNoPhysics() {
        return true;
    }
}
