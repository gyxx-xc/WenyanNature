package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.block.RunnerBlock;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.JavacallHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockMoveHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE = {WenyanType.INT, WenyanType.INT, WenyanType.INT};

    public BlockMoveHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        args.set(0, Math.max(-10, Math.min(10, (int) args.get(0))));
        args.set(1, Math.max(-10, Math.min(10, (int) args.get(1))));
        args.set(2, Math.max(-10, Math.min(10, (int) args.get(2))));

        if (context.runnerWarper().runner() instanceof BlockRunner runner) {
            Level level = runner.getLevel();
            BlockPos newPos = runner.getBlockPos().offset((int) args.get(0), (int) args.get(1), (int) args.get(2));
            BlockPos attach = newPos.relative(RunnerBlock.getConnectedDirection(runner.getBlockState()).getOpposite());
            assert level != null;
            if (!level.getBlockState(attach).isCollisionShapeFullBlock(level, attach)) return WenyanValue.NULL;
            level.setBlockAndUpdate(newPos, runner.getBlockState());
            level.neighborChanged(newPos, runner.getBlockState().getBlock(), newPos);
            BlockRunner entity = (BlockRunner) level.getBlockEntity(newPos);
            assert entity != null;
            entity.copy((BlockRunner) level.getBlockEntity(runner.getBlockPos()));

            level.setBlockAndUpdate(runner.getBlockPos(), Blocks.AIR.defaultBlockState());
            level.neighborChanged(runner.getBlockPos(), runner.getBlockState().getBlock(), runner.getBlockPos());
            level.removeBlockEntity(runner.getBlockPos());
        } else {
            // TODO translatable
            throw new WenyanException.WenyanTypeException("BlockMoveHandler can only be used with BlockRunner.");
        }

        return WenyanValue.NULL;
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
