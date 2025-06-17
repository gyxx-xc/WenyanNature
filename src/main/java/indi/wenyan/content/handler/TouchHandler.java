package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;

import java.util.List;

public class TouchHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.INT, WenyanType.INT, WenyanType.INT};

    public TouchHandler() {
        super();
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        int dx = Math.max(-10, Math.min(10, (int) args.get(0)));
        int dy = Math.max(-10, Math.min(10, (int) args.get(1)));
        int dz = Math.max(-10, Math.min(10, (int) args.get(2)));
        if (context.runnerWarper().runner() instanceof BlockRunner runner) {
            BlockPos blockPos = runner.getBlockPos().offset(dx, dy, dz);
            context.holder().level().getProfiler().push("explosion_blocks");
            context.holder().level().getBlockState(blockPos).onExplosionHit(context.holder().level(), blockPos,
                    new Explosion(context.holder().level(), null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                            1.0f, false, Explosion.BlockInteraction.TRIGGER_BLOCK), (a1, a2) -> {
                    });
            context.holder().level().getProfiler().pop();
        }
        return WenyanValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
