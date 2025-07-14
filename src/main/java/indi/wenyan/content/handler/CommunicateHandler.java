package indi.wenyan.content.handler;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.phys.Vec3;

public class CommunicateHandler implements IExecCallHandler {
    public static final WenyanType<?>[] ARG_TYPES =
            {WenyanInteger.TYPE, WenyanInteger.TYPE, WenyanInteger.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        if (context.runnerWarper().runner() instanceof RunnerBlockEntity runner) {
            runner.communicate = new Vec3(
                    Math.max(-10, Math.min(10, context.args().get(0).as(WenyanInteger.TYPE).value())),
                    Math.max(-10, Math.min(10, context.args().get(1).as(WenyanInteger.TYPE).value())),
                    Math.max(-10, Math.min(10, context.args().get(2).as(WenyanInteger.TYPE).value())));
            runner.isCommunicating = true;
            assert runner.getLevel() != null;
            runner.getLevel().sendBlockUpdated(runner.getBlockPos(), runner.getBlockState(), runner.getBlockState(), 3);
        }
        return WenyanNull.NULL;
    }
}
