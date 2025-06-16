package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.level.Level;

public class ExplosionHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE};

    public ExplosionHandler() {
        super();
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        var args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                    (float) Math.max(1, Math.min(20, (double) args.getFirst())), Level.ExplosionInteraction.MOB);
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
