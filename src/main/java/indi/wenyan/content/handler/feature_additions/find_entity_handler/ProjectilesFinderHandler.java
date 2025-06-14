package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className ProjectileFinderHandler
 * @Description TODO 气踪之大寻
 * @date 2025/6/13 13:54
 */
public final class ProjectilesFinderHandler extends EntitysFinderHandler<Projectile>{
    public ProjectilesFinderHandler(Level level) {
        super(level, Projectile.class);
    }
}
