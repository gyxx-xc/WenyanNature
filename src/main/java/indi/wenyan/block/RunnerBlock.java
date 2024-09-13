package indi.wenyan.block;

import com.mojang.serialization.MapCodec;
import indi.wenyan.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RunnerBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<RunnerBlock> CODEC = simpleCodec(RunnerBlock::new);
    public static final Properties PROPERTIES =
            BlockBehaviour.Properties.of()
            .strength(1.0F)
            .sound(SoundType.WOOL)
            .noOcclusion();
    public static final VoxelShape SHAPE = Block.box(6,0,2,11,1,13);

    @Override
    protected @NotNull MapCodec<RunnerBlock> codec() {
        return CODEC;
    }

    public RunnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        blockState.getProperties();
        return new BlockRunner(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (type, pos, state1, entity) -> {
            if (blockEntityType == Registration.BLOCK_RUNNER.get())
                BlockRunner.tick(level, pos, state1, (BlockRunner) entity);
        };
    }


}

