package indi.wenyan.content.block.additional_module;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.runner.RunnerBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractAdditionalModuleBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
    public static final Properties PROPERTIES = Properties.of();

    public static final MapCodec<RunnerBlock> CODEC = simpleCodec((ignore)->new RunnerBlock());
    public static final VoxelShape FLOOR_NORTH_AABB;
    public static final VoxelShape FLOOR_SOUTH_AABB;
    public static final VoxelShape FLOOR_WEST_AABB;
    public static final VoxelShape FLOOR_EAST_AABB;
    public static final VoxelShape CEILING_NORTH_AABB;
    public static final VoxelShape CEILING_SOUTH_AABB;
    public static final VoxelShape CEILING_WEST_AABB;
    public static final VoxelShape CEILING_EAST_AABB;
    public static final VoxelShape NORTH_AABB;
    public static final VoxelShape SOUTH_AABB;
    public static final VoxelShape WEST_AABB;
    public static final VoxelShape EAST_AABB;

    public AbstractAdditionalModuleBlock() {
        super(PROPERTIES);
    }

    @Override
    protected @NotNull MapCodec<RunnerBlock> codec() {
        return CODEC;
    }

    abstract BlockEntityType<?> getType();

    @Override
    public @NotNull VoxelShape
    getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        switch (pState.getValue(FACE)) {
            case FLOOR:
                switch (direction) {
                    case NORTH:
                        return FLOOR_NORTH_AABB;
                    case SOUTH:
                        return FLOOR_SOUTH_AABB;
                    case WEST:
                        return FLOOR_WEST_AABB;
                    case EAST:
                        return FLOOR_EAST_AABB;
                }
                break;
            case WALL:
                return switch (direction) {
                    case EAST -> EAST_AABB;
                    case WEST -> WEST_AABB;
                    case SOUTH -> SOUTH_AABB;
                    case NORTH, UP, DOWN -> NORTH_AABB;
                };
            case CEILING:
                switch (direction) {
                    case NORTH:
                        return CEILING_NORTH_AABB;
                    case SOUTH:
                        return CEILING_SOUTH_AABB;
                    case WEST:
                        return CEILING_WEST_AABB;
                    case EAST:
                        return CEILING_EAST_AABB;
                }
                break;
            default:
                throw new MatchException(null, null);
        }
        throw new MatchException(null, null);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    public static @NotNull Direction getConnectedDirection(BlockState state) {
        return switch (state.getValue(FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.getValue(FACING);
        };
    }

    static {
        FLOOR_NORTH_AABB = box(6, 0, 3, 11, 1, 13);
        FLOOR_SOUTH_AABB = box(5, 0, 3, 10, 1, 13);
        FLOOR_WEST_AABB = box(3, 0, 5, 13, 1, 10);
        FLOOR_EAST_AABB = box(3, 0, 6, 13, 1, 11);
        CEILING_NORTH_AABB = box(5, 15, 3, 10, 16, 13);
        CEILING_SOUTH_AABB = box(6, 15, 3, 11, 16, 13);
        CEILING_WEST_AABB = box(3, 15, 6, 13, 16, 11);
        CEILING_EAST_AABB = box(3, 15, 5, 13, 16, 10);
        NORTH_AABB = box(6, 3, 15, 11, 13, 16);
        SOUTH_AABB = box(5, 3, 0, 10, 13, 1);
        WEST_AABB = box(15, 3, 5, 16, 13, 10);
        EAST_AABB = box(0, 3, 6, 1, 13, 11);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getType().create(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == getType())
                ((AbstractAdditionalModuleEntity) entity).handle();
        };
    }
}
