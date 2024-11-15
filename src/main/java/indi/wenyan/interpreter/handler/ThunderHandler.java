package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

public class ThunderHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public ThunderHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }
    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) {
        HandlerEntity.levelRun(holder.level(), (level) -> {
            Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            e.moveTo(entity.getX(), entity.getY(), entity.getZ());
            level.addFreshEntity(e);
        });
        return null;
    }
}
