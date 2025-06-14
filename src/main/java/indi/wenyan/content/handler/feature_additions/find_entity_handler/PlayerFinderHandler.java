package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className PlayerFinderHandler
 * @Description TODO 真觅
 * @date 2025/6/13 13:54
 */
public final class PlayerFinderHandler extends EntityFinderHandler<Player>{
    public PlayerFinderHandler(Level level) {
        super(level, Player.class);
    }
}
