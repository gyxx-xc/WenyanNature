package indi.wenyan.entity;

import indi.wenyan.WenyanNature;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Semaphore;

public class HandRunnerEntity extends Entity {
    public Semaphore semaphore;
    public Thread program;
    public String code;
    public Player holder;

    public HandRunnerEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && semaphore != null) {
            semaphore.release(1);
            if (!program.isAlive())
                this.remove(RemovalReason.DISCARDED);
        }
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
                    new WenyanMainVisitor(WenyanPackages.handEnvironment(holder), semaphore)
                            .visit(WenyanVisitor.program(code)));
            program.setUncaughtExceptionHandler(exceptionHandler);
            program.start();
        }
        super.onAddedToLevel();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }
}