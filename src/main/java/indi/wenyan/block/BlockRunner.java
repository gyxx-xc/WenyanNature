package indi.wenyan.block;

import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.Semaphore;

public class BlockRunner extends BlockEntity {
    public BlockRunner(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockRunner entity){
    }

    public Boolean isRunning;
    public Semaphore semaphore;
    public Thread program;
}
