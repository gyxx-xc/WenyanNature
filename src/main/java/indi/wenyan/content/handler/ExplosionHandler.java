package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExplosionHandler implements JavacallHandler {
    public final HandRunnerEntity entity;
    public final Player holder;

    public static final WenyanNativeValue.Type[] ARGS_TYPE =
            {WenyanNativeValue.Type.DOUBLE};

    public ExplosionHandler(HandRunnerEntity entity, Player holder) {
        super();
        this.entity = entity;
        this.holder = holder;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyan_args) throws WenyanException.WenyanTypeException {
        Object[] args = JavacallHandler.getArgs(wenyan_args, ARGS_TYPE);
        args[0] = Math.max(1, Math.min(20, (double) args[0]));
        holder.level().explode(holder, entity.getX(), entity.getY(), entity.getZ(),
                (float) (double) args[0], Level.ExplosionInteraction.MOB);
        return WenyanNativeValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
