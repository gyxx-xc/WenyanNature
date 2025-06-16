package indi.wenyan.content.handler;

import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.phys.Vec3;

public class BulletHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.INT};

    public BulletHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        var newArgs = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);

        Vec3 dir = new Vec3(
                Math.max(-10, Math.min(10, (double) newArgs.get(0))),
                Math.max(-10, Math.min(10, (double) newArgs.get(1))),
                Math.max(-10, Math.min(10, (double) newArgs.get(2))));

        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity) {
            BulletEntity bullet = new BulletEntity(entity.level(), entity.getPosition(0), dir,
                    Math.max(1, Math.min(20, (double) newArgs.get(3))) / 10,
                    Math.max(1, Math.min(200, (int) newArgs.get(4))), context.holder());
            entity.level().addFreshEntity(bullet);
        }
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
