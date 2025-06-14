package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className MonstersFinderHandler
 * @Description TODO 煞觅之大寻
 * @date 2025/6/6 15:56
 */
public final class MonstersFinderHandler extends EntitysFinderHandler<Monster> {
    public MonstersFinderHandler(Level level) {
        super(level,Monster.class);
    }
}
