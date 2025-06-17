package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.world.phys.Vec3;

public class CommunicateHandler implements JavacallHandler {
    public static final WenyanType[] ARG_TYPES =
            {WenyanType.INT, WenyanType.INT, WenyanType.INT};

    public CommunicateHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
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
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
