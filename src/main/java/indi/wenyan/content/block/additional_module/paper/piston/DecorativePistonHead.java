package indi.wenyan.content.block.additional_module.paper.piston;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class DecorativePistonHead extends DirectionalBlock {
    public static final String ID = "decorative_piston_head";

    public static final MapCodec<DecorativePistonHead> CODEC = simpleCodec(DecorativePistonHead::new);
    public static final EnumProperty<PistonType> TYPE;
    private static final Map<Direction, VoxelShape> SHAPES;

    protected @NonNull MapCodec<DecorativePistonHead> codec() {
        return CODEC;
    }

    public DecorativePistonHead(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, PistonType.DEFAULT));
    }

    protected boolean useShapeForLightOcclusion(@NonNull BlockState state) {
        return true;
    }

    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return (SHAPES).get(state.getValue(FACING));
    }

    @Override
    protected void neighborChanged(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS | UPDATE_KNOWN_SHAPE | UPDATE_INVISIBLE);
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    protected @NonNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType type) {
        return false;
    }

    static {
        TYPE = BlockStateProperties.PISTON_TYPE;
        SHAPES = Shapes.rotateAll(Shapes.or(Block.boxZ(16.0F, 0.0F, 4.0F), Block.boxZ(4.0F, 4.0F, 20.0F)));
    }
}
