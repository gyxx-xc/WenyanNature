package indi.wenyan.interpreter.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CommunicateHandler extends JavacallHandler {
    private final BlockPos pos;
    private final BlockState state;
    private final Level level;

    public static final WenyanValue.Type[] ARG_TYPES =
            {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};

    public CommunicateHandler(BlockPos pos, BlockState state, Level level) {
        this.pos = pos;
        this.state = state;
        this.level = level;
    }

    @Override
    public WenyanValue handle(WenyanValue[] wenyanValues) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyanValues, ARG_TYPES);
        args[0] = Math.max(-10, Math.min(10, (int) args[0]));
        args[1] = Math.max(-10, Math.min(10, (int) args[1]));
        args[2] = Math.max(-10, Math.min(10, (int) args[2]));
        HandlerEntity.levelRun(level, level -> {
            BlockRunner blockRunner = (BlockRunner) level.getBlockEntity(pos);
            assert blockRunner != null;
            blockRunner.communicate = new Vec3((int) args[0], (int) args[1], (int) args[2]);
            blockRunner.isCommunicating = true;
            level.sendBlockUpdated(pos, state, state, 3);
        });
        return null;
    }
}
