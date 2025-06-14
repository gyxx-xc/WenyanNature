package indi.wenyan.content.handler.feature_additions.find_entity_handler;

import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className VehiclesFinderHandler
 * @Description TODO 天工之大寻
 * @date 2025/6/13 13:54
 */
public final class VehiclesFinderHandler extends EntitysFinderHandler<VehicleEntity>{
    public VehiclesFinderHandler(Level level) {
        super(level, VehicleEntity.class);
    }
}
