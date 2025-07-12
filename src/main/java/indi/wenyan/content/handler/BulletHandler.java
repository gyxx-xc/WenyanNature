package indi.wenyan.content.handler;

import indi.wenyan.content.entity.BulletEntity;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.phys.Vec3;

public class BulletHandler implements IExecCallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanInteger.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanTypeException {
        Vec3 dir = new Vec3(
                Math.max(-10, Math.min(10, context.args().get(0).as(WenyanDouble.TYPE).value())),
                Math.max(-10, Math.min(10, context.args().get(1).as(WenyanDouble.TYPE).value())),
                Math.max(-10, Math.min(10, context.args().get(2).as(WenyanDouble.TYPE).value())));

        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity) {
            BulletEntity bullet = new BulletEntity(entity.level(), entity.getPosition(0),
                    dir, Math.max(1,
                    Math.min(20, context.args().get(3).as(WenyanDouble.TYPE).value())) / 10,
                    Math.max(1, Math.min(200, context.args().get(4).as(WenyanInteger.TYPE).value())),
                    context.holder());
            entity.level().addFreshEntity(bullet);
        }
        return WenyanNull.NULL;
    }
}
