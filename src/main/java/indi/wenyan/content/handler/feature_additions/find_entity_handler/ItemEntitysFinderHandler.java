package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className ItemEntitysFinderHandler
 * @Description TODO 器象之大寻
 * @date 2025/6/6 15:56
 */
public final class ItemEntitysFinderHandler extends EntitysFinderHandler<ItemEntity> {
    public ItemEntitysFinderHandler(Level level) {
        super(level,ItemEntity.class);
    }
}
