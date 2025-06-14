package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className AnimalsFinderHandler
 * @Description TODO 兽觅之大寻
 * @date 2025/6/13 13:54
 */
public final class AnimalsFinderHandler extends EntitysFinderHandler<Animal>{
    public AnimalsFinderHandler(Level level) {
        super(level, Animal.class);
    }
}
