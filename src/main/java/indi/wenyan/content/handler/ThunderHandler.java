package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

public class ThunderHandler implements JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public ThunderHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }
    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) {
            Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, entity.level());
            e.moveTo(entity.getX(), entity.getY(), entity.getZ());
            entity.level().addFreshEntity(e);
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
