package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.content.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExplosionHandler extends JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.DOUBLE};

    public ExplosionHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }

    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanTypeException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        args[0] = Math.max(1, Math.min(20, (double) args[0]));
        HandlerEntity.levelRun(holder.level(), (level) -> {
            if (!level.isClientSide())
                level.explode(holder, entity.getX(), entity.getY(), entity.getZ(),
                        (float) (double) args[0], Level.ExplosionInteraction.MOB);
        });
        return null;
    }
}
