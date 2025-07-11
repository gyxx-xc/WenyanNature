package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import net.minecraft.world.phys.Vec3;

public class CommunicateHandler implements IJavacallHandler {
    public static final WenyanType<?>[] ARG_TYPES =
            {WenyanInteger.TYPE, WenyanInteger.TYPE, WenyanInteger.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        var args = JavacallHandlers.getArgs(context.args(), ARG_TYPES);
        if (context.runnerWarper().runner() instanceof BlockRunner runner) {
            runner.communicate = new Vec3(
                    Math.max(-10, Math.min(10, (int) args.get(0))),
                    Math.max(-10, Math.min(10, (int) args.get(1))),
                    Math.max(-10, Math.min(10, (int) args.get(2))));
            runner.isCommunicating = true;
            assert runner.getLevel() != null;
            runner.getLevel().sendBlockUpdated(runner.getBlockPos(), runner.getBlockState(), runner.getBlockState(), 3);
        }
        return WenyanNull.NULL;
    }
}
