package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MoveHandler implements IJavacallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> newArgs = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        newArgs.set(0, Math.max(-20, Math.min(20, (double) newArgs.get(0))));
        newArgs.set(1, Math.max(-20, Math.min(20, (double) newArgs.get(1))));
        newArgs.set(2, Math.max(-20, Math.min(20, (double) newArgs.get(2))));
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.setDeltaMovement(new Vec3((double) newArgs.get(0)/10,
                    (double) newArgs.get(1)/10, (double) newArgs.get(2)/10));
        return WenyanNull.NULL;
    }
}
