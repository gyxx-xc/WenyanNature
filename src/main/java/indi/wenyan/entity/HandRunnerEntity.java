package indi.wenyan.entity;

import indi.wenyan.WenyanNature;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import indi.wenyan.setup.Registration;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

public class HandRunnerEntity extends ThrowableProjectile {
    public Semaphore semaphore;
    public Thread program;
    public String code;
    public Player holder;

    public HandRunnerEntity(EntityType<HandRunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HandRunnerEntity(@NotNull Player holder, String code) {
        super(Registration.HAND_RUNNER_ENTITY.get(), holder.level());
        this.holder = holder;
        this.code = code;
        Vec3 lookDirection = Vec3.directionFromRotation(holder.getXRot(), holder.getYRot()).normalize().scale(0.5);
        this.moveTo(holder.getEyePosition().add(lookDirection.x, -0.5, lookDirection.z));
        this.shoot(lookDirection.x, lookDirection.y+0.5, lookDirection.z, 0.05F, 0.1F);
    }

    @Override
    public void tick() {
        if (getDeltaMovement().length() < 0.001)
            setDeltaMovement(Vec3.ZERO);
        else
            setDeltaMovement(getDeltaMovement().scale(0.8));
        super.tick();
        if (!this.level().isClientSide() && semaphore != null) {
            semaphore.release(1);
            if (!program.isAlive())
                this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (reason.shouldDestroy() && program != null && program.isAlive())
            program.interrupt();
        super.remove(reason);
    }

    @Override
    public void onAddedToLevel() {
        if (!this.level().isClientSide()) {
            Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
                if (e instanceof WenyanException) {
                    holder.sendSystemMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
                } else {
                    holder.sendSystemMessage(Component.literal("Error").withStyle(ChatFormatting.RED));
                    WenyanNature.LOGGER.info("Error: {}", e.getMessage());
                }
            };
            // ready to visit
            semaphore = new Semaphore(0);
            program = new Thread(() ->
                    new WenyanMainVisitor(WenyanPackages.handEnvironment(holder, this), semaphore)
                            .visit(WenyanVisitor.program(code)));
            program.setUncaughtExceptionHandler(exceptionHandler);
            program.start();
        }
        super.onAddedToLevel();
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

}
