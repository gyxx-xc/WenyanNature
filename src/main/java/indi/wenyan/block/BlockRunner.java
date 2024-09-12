package indi.wenyan.block;

import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.Semaphore;

public class BlockRunner extends BlockEntity {
    public BlockRunner(BlockPos pos, BlockState blockState) {
        super(Registration.BLOCK_RUNNER.get(), pos, blockState);
    }

    public Boolean isRunning;
    public String code;
    public Semaphore semaphore;
    public Thread program;
}
