package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className LivingEntityFinderHandler
 * @Description TODO 灵察
 * @date 2025/6/6 15:56
 */
public final class LivingEntityFinderHandler extends EntityFinderHandler<LivingEntity> {
    public LivingEntityFinderHandler(Level level) {
        super(level, LivingEntity.class);
    }
}
