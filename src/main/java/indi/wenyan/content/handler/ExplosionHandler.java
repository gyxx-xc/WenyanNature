package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.WenyanDouble;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import net.minecraft.world.level.Level;

public class ExplosionHandler implements JavacallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE};

    @Override
    public WenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        var args = JavacallHandler.getArgs(context.args(), ARGS_TYPE);
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                    (float) Math.max(1, Math.min(20, (double) args.getFirst())), Level.ExplosionInteraction.MOB);
        return WenyanNull.NULL;
    }
}
