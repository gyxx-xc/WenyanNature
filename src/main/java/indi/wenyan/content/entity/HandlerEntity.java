package indi.wenyan.content.entity;

import indi.wenyan.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * This entity is just for a temporary use.
 * Since the handler will cause thread blocked,
 * this entity can be used to handle the function in main thread.
 */
@ParametersAreNonnullByDefault
public class HandlerEntity extends Entity {
    public Consumer<Level> function;

    public HandlerEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (function != null)
            function.accept(level());
        discard();
    }

    public static void levelRun(Level level, Consumer<Level> function) {
        HandlerEntity entity = new HandlerEntity(Registration.HANDLER_ENTITY.get(), level);
        entity.function = function;
        level.addFreshEntity(entity);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
