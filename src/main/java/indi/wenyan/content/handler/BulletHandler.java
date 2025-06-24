package indi.wenyan.content.handler;

import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.world.phys.Vec3;

public class BulletHandler implements IJavacallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanInteger.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        var newArgs = IJavacallHandler.getArgs(context.args(), ARGS_TYPE);

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
        return WenyanNull.NULL;
    }
}
