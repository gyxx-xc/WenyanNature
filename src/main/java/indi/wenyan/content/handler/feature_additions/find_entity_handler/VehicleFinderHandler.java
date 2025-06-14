package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className VehicleFinderHandler
 * @Description TODO 天工
 * @date 2025/6/13 13:54
 */
public final class VehicleFinderHandler extends EntityFinderHandler<VehicleEntity>{
    public VehicleFinderHandler(Level level) {
        super(level, VehicleEntity.class);
    }
}
