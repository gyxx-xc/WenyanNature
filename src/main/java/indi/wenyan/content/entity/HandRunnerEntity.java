package indi.wenyan.content.entity;

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

import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class HandRunnerEntity extends Projectile {
    public Semaphore entitySemaphore;
    public Semaphore programSemaphore;
    public Thread program;
    public String code;
    public Player holder;
    public boolean isRunning = false;
    public int speed;

    public HandRunnerEntity(EntityType<HandRunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public HandRunnerEntity(@NotNull Player holder, String code, int level) {
        super(Registration.HAND_RUNNER_ENTITY.get(), holder.level());
        this.holder = holder;
        this.code = code;
        speed = (int) Math.pow(10, level);
        Vec3 lookDirection = Vec3.directionFromRotation(holder.getXRot(), holder.getYRot()).normalize().scale(0.5);
        this.moveTo(holder.getEyePosition().add(lookDirection.x, -0.5, lookDirection.z));
        this.shoot(lookDirection.x, lookDirection.y+0.5, lookDirection.z, 0.1F, 10F);
        addDeltaMovement(holder.getDeltaMovement());
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && isRunning) {
            if (program == null) {
                discard();
            }
            else {
                if (!program.isAlive())
                    discard();
                programSemaphore.release(speed);
                try {
                    entitySemaphore.acquire(speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!isRunning) {
            if (getDeltaMovement().length() < 0.01) {
                setDeltaMovement(Vec3.ZERO);
                run();
            } else {
                setDeltaMovement(getDeltaMovement().scale(0.5));
            }
        } else {
            setDeltaMovement(getDeltaMovement().length() < 0.01 ? Vec3.ZERO : getDeltaMovement().scale(0.95));
        }
        checkInsideBlocks();
        updateRotation();
        setPos(position().add(getDeltaMovement()));

        super.tick();
    }

    public void run() {
        if (!this.level().isClientSide()) {
            Thread.UncaughtExceptionHandler exceptionHandler = (t, e) -> {
                entitySemaphore.release(100000);
                if (e instanceof WenyanException) {
                    holder.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), true);
                } else {
                    holder.displayClientMessage(Component.literal("Error").withStyle(ChatFormatting.RED), true);
                    WenyanNature.LOGGER.info("Error: {}", e.getMessage());
                    e.printStackTrace((PrintWriter) WenyanNature.LOGGER);
                }
            };
            // ready to visit
            programSemaphore = new Semaphore(0);
            entitySemaphore = new Semaphore(0);
            program = new Thread(() -> {
                new WenyanMainVisitor(WenyanPackages.handEnvironment(holder, this), programSemaphore, entitySemaphore)
                        .visit(WenyanVisitor.program(code));
                entitySemaphore.release(100000);});
            program.setUncaughtExceptionHandler(exceptionHandler);
            program.start();
            if (program.isAlive())
                try {
                    entitySemaphore.acquire(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
        isRunning = true;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (reason.shouldDestroy() && program != null)
            program.interrupt();
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
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

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
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("isRunning", isRunning);
        super.addAdditionalSaveData(compound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        isRunning = compound.getBoolean("isRunning");
        super.readAdditionalSaveData(compound);
    }
}
