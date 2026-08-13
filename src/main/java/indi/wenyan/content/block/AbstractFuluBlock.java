package indi.wenyan.content.block;

import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractFuluBlock extends FaceAttachedHorizontalDirectionalBlock {

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

    protected AbstractFuluBlock(Properties properties) {
        super(properties.noCollision().instabreak());
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FACE, AttachFace.FLOOR)
        );
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
        FLOOR_NORTH_AABB = box(4, 0, 0, 12, 1, 16);
        FLOOR_SOUTH_AABB = box(4, 0, 0, 12, 1, 16);
        FLOOR_WEST_AABB = box(0, 0, 4, 16, 1, 12);
        FLOOR_EAST_AABB = box(0, 0, 4, 16, 1, 12);
        CEILING_NORTH_AABB = box(4, 15, 0, 12, 16, 16);
        CEILING_SOUTH_AABB = box(4, 15, 0, 12, 16, 16);
        CEILING_WEST_AABB = box(0, 15, 4, 16, 16, 12);
        CEILING_EAST_AABB = box(0, 15, 4, 16, 16, 12);
        NORTH_AABB = box(4, 0, 15, 12, 16, 16);
        SOUTH_AABB = box(4, 0, 0, 12, 16, 1);
        WEST_AABB = box(15, 0, 4, 16, 16, 12);
        EAST_AABB = box(0, 0, 4, 1, 16, 12);
    }
}
