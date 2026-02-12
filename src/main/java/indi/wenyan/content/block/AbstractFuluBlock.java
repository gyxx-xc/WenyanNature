package indi.wenyan.content.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractFuluBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final Properties PROPERTIES = Properties.of().noCollission();

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

    protected AbstractFuluBlock() {
        super(PROPERTIES);
    }

    protected AbstractFuluBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return !level.getBlockState(pos.relative(direction))
                .getCollisionShape(level, pos.relative(direction)).isEmpty();
    }

    @Override
    public VoxelShape
    getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        return switch (pState.getValue(FACE)) {
            case FLOOR -> switch (direction) {
                case NORTH -> FLOOR_NORTH_AABB;
                case SOUTH -> FLOOR_SOUTH_AABB;
                case WEST -> FLOOR_WEST_AABB;
                case EAST -> FLOOR_EAST_AABB;
                default -> throw new MatchException(null, null);
            };
            case WALL -> switch (direction) {
                case EAST -> EAST_AABB;
                case WEST -> WEST_AABB;
                case SOUTH -> SOUTH_AABB;
                case NORTH, UP, DOWN -> NORTH_AABB;
            };
            case CEILING -> switch (direction) {
                case NORTH -> CEILING_NORTH_AABB;
                case SOUTH -> CEILING_SOUTH_AABB;
                case WEST -> CEILING_WEST_AABB;
                case EAST -> CEILING_EAST_AABB;
                default -> throw new MatchException(null, null);
            };
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    public static Direction getConnectedDirection(BlockState state) {
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
}
