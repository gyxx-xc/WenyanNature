package indi.wenyan.handler;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import indi.wenyan.setup.Registration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ThunderHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public ThunderHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }
    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanTypeException {
        HandlerEntity.levelRun(holder.level(), (level) -> {
            Entity e = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            e.moveTo(entity.getX(), entity.getY(), entity.getZ());
            level.addFreshEntity(e);
        });
        return null;
    }
}
