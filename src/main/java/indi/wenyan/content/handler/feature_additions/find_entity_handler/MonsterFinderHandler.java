package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className MonsterFinderHandler
 * @Description TODO 煞觅
 * @date 2025/6/9 15:57
 */
public final class MonsterFinderHandler extends EntityFinderHandler<Monster> {
    public MonsterFinderHandler(Level level) {
        super(level, Monster.class);
    }
}
