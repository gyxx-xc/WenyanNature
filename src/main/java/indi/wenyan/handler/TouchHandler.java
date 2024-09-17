package indi.wenyan.handler;

import indi.wenyan.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class TouchHandler extends JavacallHandler {
    public final Level level;
    public final BlockPos pos;

    public static final WenyanValue.Type[] ARGS_TYPE =
            {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};

    public TouchHandler(Level level, BlockPos pos, Player holder) {
        super();
        this.level = level;
        this.pos = pos;
    }

    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        BlockPos blockPos = pos.offset((int) args[0], (int) args[1], (int) args[2]);
        HandlerEntity.levelRun(level, (level) -> {
            level.getProfiler().push("explosion_blocks");
            level.getBlockState(blockPos).onExplosionHit(level, blockPos,
                    new Explosion(level, null, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                            1.0f, false, Explosion.BlockInteraction.TRIGGER_BLOCK), (a1, a2) -> {});
            level.getProfiler().pop();
        });
        return null;
    }
}
