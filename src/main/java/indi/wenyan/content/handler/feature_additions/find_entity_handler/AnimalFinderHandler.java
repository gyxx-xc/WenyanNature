package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className AnimalFinderHandler
 * @Description TODO 兽觅
 * @date 2025/6/13 13:54
 */
public final class AnimalFinderHandler extends EntityFinderHandler<Animal>{
    public AnimalFinderHandler(Level level) {
        super(level, Animal.class);
    }
}
