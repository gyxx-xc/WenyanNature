package indi.wenyan.content.block;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.gui.TextFieldScreen;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockRunnerCodePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class
RunnerBlock extends FaceAttachedHorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<RunnerBlock> CODEC = simpleCodec((ignore)->new RunnerBlock());
    public static final Properties PROPERTIES;
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

    @OnlyIn(Dist.CLIENT)
    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var runner = (BlockRunner) level.getBlockEntity(pos);
        assert runner != null;
        if (player.isShiftKeyDown()) {
            if (level.isClientSide())
                Minecraft.getInstance().setScreen(new TextFieldScreen(runner.pages, content -> {
                    runner.pages = content;
                    PacketDistributor.sendToServer(new BlockRunnerCodePacket(pos, content));
                }));
        } else {
            if (!level.isClientSide()) {
                var maybeCrafting = level.getBlockEntity(
                        pos.relative(getConnectedDirection(state).getOpposite()));

                if (maybeCrafting instanceof CraftingBlockEntity cb) {
                    cb.run(runner, player);
                } else {
                    runner.run(player);
                }
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull MapCodec<RunnerBlock> codec() {
        return CODEC;
    }

    public RunnerBlock() {
        super(PROPERTIES);
    }

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        blockState.getProperties();
        return new BlockRunner(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T>
    getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == Registration.BLOCK_RUNNER.get())
                BlockRunner.tick(level1, pos, state1, (BlockRunner) entity);
        };
    }

    public static @NotNull Direction getConnectedDirection(BlockState state) {
        return switch (state.getValue(FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.getValue(FACING);
        };
    }


    static {
        PROPERTIES = BlockBehaviour.Properties.of()
                .strength(1.0F)
                .sound(SoundType.WOOL)
                .instabreak()
                .noOcclusion();
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
