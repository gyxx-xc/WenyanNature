package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class MoveHandler implements IExecCallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Double> newArgs = Lists.newArrayList();
        newArgs.add(Math.max(-20, Math.min(20, context.args().get(0).as(WenyanDouble.TYPE).value())));
        newArgs.add(Math.max(-20, Math.min(20, context.args().get(1).as(WenyanDouble.TYPE).value())));
        newArgs.add(Math.max(-20, Math.min(20, context.args().get(2).as(WenyanDouble.TYPE).value())));
        if (context.runnerWarper().runner() instanceof HandRunnerEntity entity)
            entity.setDeltaMovement(new Vec3(newArgs.get(0) /10,
                    newArgs.get(1) /10, newArgs.get(2) /10));
        return WenyanNull.NULL;
    }
}
