package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.level.Level;

public class ExplosionHandler implements IJavacallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        var args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(),
                    (float) Math.max(1, Math.min(20, (double) args.getFirst())), Level.ExplosionInteraction.MOB);
        return WenyanNull.NULL;
    }
}
