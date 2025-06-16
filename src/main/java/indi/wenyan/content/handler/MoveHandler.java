package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MoveHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE};

    public MoveHandler() {
        super();
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> newArgs = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        newArgs.set(0, Math.max(-20, Math.min(20, (double) newArgs.get(0))));
        newArgs.set(1, Math.max(-20, Math.min(20, (double) newArgs.get(1))));
        newArgs.set(2, Math.max(-20, Math.min(20, (double) newArgs.get(2))));
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.setDeltaMovement(new Vec3((double) newArgs.get(0)/10,
                    (double) newArgs.get(1)/10, (double) newArgs.get(2)/10));
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
