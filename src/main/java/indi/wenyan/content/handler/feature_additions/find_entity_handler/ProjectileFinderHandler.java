package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className ProjectileFinderHandler
 * @Description TODO 气踪
 * @date 2025/6/13 13:54
 */
public final class ProjectileFinderHandler extends EntityFinderHandler<Projectile>{
    public ProjectileFinderHandler(Level level) {
        super(level, Projectile.class);
    }
}
