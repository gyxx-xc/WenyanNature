package indi.wenyan.setup.definitions;

import indi.wenyan.content.entity.ThrowRunnerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static indi.wenyan.WenyanProgramming.MODID;

public enum WenyanEntities {
    ;
    public static final DeferredRegister.Entities DR = DeferredRegister.createEntities(MODID);
    public static final Supplier<EntityType<ThrowRunnerEntity>> THROW_RUNNER_ENTITY = DR.registerEntityType(ThrowRunnerEntity.ID,
            ThrowRunnerEntity::new, MobCategory.MISC,
            builder -> builder
                    .noLootTable()
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10));
}
