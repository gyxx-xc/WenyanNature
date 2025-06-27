package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;

import java.util.List;

public class TouchHandler implements IJavacallHandler {
    public static final WenyanType<?>[] ARGS_TYPE =
            {WenyanInteger.TYPE, WenyanInteger.TYPE, WenyanInteger.TYPE};

    @Override
    public IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
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
        return WenyanNull.NULL;
    }
}
