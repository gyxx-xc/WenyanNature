package indi.wenyan.content.block.power;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PowerBlock extends Block implements EntityBlock {
    public static final String ID = "power_block";
    public static final Properties PROPERTIES = Properties.of();

    public PowerBlock() {
        super(PROPERTIES);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == Registration.RUNNER_BLOCK_ENTITY.get())
                ((RunnerBlockEntity) entity).tick(level1, pos, state1);
        };
    }

}
