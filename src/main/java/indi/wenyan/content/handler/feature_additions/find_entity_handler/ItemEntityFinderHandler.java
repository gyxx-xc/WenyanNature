package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className ItemEntityFinderHandler
 * @Description TODO 器象
 * @date 2025/6/6 15:56
 */
public final class ItemEntityFinderHandler extends EntityFinderHandler<ItemEntity>{
    public ItemEntityFinderHandler(Level level) {
        super(level, ItemEntity.class);
    }
}
