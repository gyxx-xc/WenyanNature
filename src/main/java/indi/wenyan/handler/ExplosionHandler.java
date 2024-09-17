package indi.wenyan.handler;

import indi.wenyan.entity.HandRunnerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;

public class ExplosionHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public ExplosionHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) {
        PrimedTnt tnt = new PrimedTnt(entity.level(), entity.getX(), entity.getY(), entity.getZ(), holder);
        tnt.setFuse(0);
        entity.level().addFreshEntity(tnt);
        return null;
    }
}
