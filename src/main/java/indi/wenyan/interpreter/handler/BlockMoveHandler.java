package indi.wenyan.interpreter.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.block.RunnerBlock;
import indi.wenyan.content.entity.HandlerEntity;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class BlockMoveHandler extends JavacallHandler {
    private final Player holder;
    private final BlockPos pos;
    private final BlockState blockState;

    public static final WenyanValue.Type[] ARGS_TYPE = {WenyanValue.Type.INT, WenyanValue.Type.INT, WenyanValue.Type.INT};

    public BlockMoveHandler(Player holder, BlockPos pos, BlockState blockState) {
        this.holder = holder;
        this.pos = pos;
        this.blockState = blockState;
    }

    @Override
    public WenyanValue handle(WenyanValue[] wenyan_args) throws WenyanException.WenyanThrowException {
        Object[] args = getArgs(wenyan_args, ARGS_TYPE);
        args[0] = Math.max(-10, Math.min(10, (int) args[0]));
        args[1] = Math.max(-10, Math.min(10, (int) args[1]));
        args[2] = Math.max(-10, Math.min(10, (int) args[2]));
        HandlerEntity.levelRun(holder.level(), level -> {
            if (!level.getBlockState(pos).is(Registration.RUNNER_BLOCK.get())) return;
            BlockPos newPos = pos.offset((int) args[0], (int) args[1], (int) args[2]);
            BlockPos attach = newPos.relative(blockState.getValue(RunnerBlock.FACING).getOpposite());
            if (!level.getBlockState(attach).isCollisionShapeFullBlock(level, attach)) return;
            level.setBlockAndUpdate(newPos, blockState);
            level.neighborChanged(newPos, blockState.getBlock(), newPos);
            BlockRunner entity = (BlockRunner) level.getBlockEntity(newPos);
            assert entity != null;
            entity.copy((BlockRunner) level.getBlockEntity(pos));

            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.neighborChanged(pos, blockState.getBlock(), pos);
            level.removeBlockEntity(pos);

        });
        return null;
    }
}
