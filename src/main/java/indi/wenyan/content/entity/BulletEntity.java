package indi.wenyan.content.entity;

import indi.wenyan.setup.Registration;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class BulletEntity extends AbstractArrow {
    private int aliveTime;
    private int tickCount;
    public Player holder;

    public BulletEntity(EntityType<BulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BulletEntity(Level level, Vec3 pos, Vec3 direction, double speed, int aliveTime, Player holder) {
        super(Registration.BULLET_ENTITY.get(), level);
        this.holder = holder;
        tickCount = 0;
        this.aliveTime = aliveTime;
        this.moveTo(pos);
        this.shoot(direction.x, direction.y, direction.z, (float) speed, 0);
    }

//    @Override
//    protected void defineSynchedData(SynchedEntityData.Builder builder) {
//    }

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
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 25.0F;
        DamageSource damagesource = this.damageSources().mobProjectile(this, holder);
        if (entity.hurt(damagesource, (float) (f*getDeltaMovement().length()))) {
            if (entity.getType() == EntityType.ENDERMAN) return;
            if (entity instanceof LivingEntity livingEntity) {
                doPostHurtEffects(livingEntity);
            }
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return null;
    }

    @Override
    protected void onInsideBlock(BlockState state) {
        if (state.isCollisionShapeFullBlock(level(), blockPosition()))
            this.discard();
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
