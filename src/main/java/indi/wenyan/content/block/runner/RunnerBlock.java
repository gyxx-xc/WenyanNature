package indi.wenyan.content.block.runner;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.setup.definitions.RunnerTier;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.network.client.BlockSetScreenPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RunnerBlock extends AbstractFuluBlock implements EntityBlock {
    public static final EnumProperty<RunningState> RUNNING_STATE = EnumProperty.create("running_state",
            RunningState.class);
    @Getter
    private final RunnerTier tier;

    public RunnerBlock(RunnerTier tier, Properties properties) {
        super(properties.noCollision().lightLevel(state -> state.getValue(RUNNING_STATE).getLightLevel()));
        this.tier = tier;
        registerDefaultState(defaultBlockState()
                .setValue(RUNNING_STATE, RunningState.NOT_RUNNING));
    }

    public static final MapCodec<RunnerBlock> CODEC = RecordCodecBuilder.mapCodec(
            (i) -> i
                    .group(RunnerTier.CODEC.fieldOf("tier").forGetter(RunnerBlock::getTier),
                            propertiesCodec())
                    .apply(i, RunnerBlock::new));

    @Override
    protected @NotNull MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hitResult) {
        var entity = level.getBlockEntity(pos);
        if (!(entity instanceof RunnerBlockEntity runner)) {
            WenyanProgramming.LOGGER.error("RunnerBlock: entity is not a RunnerBlockEntity");
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown() && player instanceof ServerPlayer sp) { // serverplayer always true
                PacketDistributor.sendToPlayer(sp, new BlockSetScreenPacket(pos, "runner_block_set_screen"));
            } else {
                runner.playerRun();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RunnerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        return (level1, pos, state1, entity) -> {
            if (blockEntityType == WenyanBlocks.RUNNER_BLOCK_ENTITY.get())
                ((RunnerBlockEntity) entity).tick(level1, pos, state1);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RUNNING_STATE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var blockState = super.getStateForPlacement(context);
        if (blockState == null) return null;
        blockState.setValue(RUNNING_STATE, RunningState.NOT_RUNNING);
        return blockState;
    }

    public enum RunningState implements StringRepresentable {
        RUNNING("running", 4, 0),
        IDLE("idle", 4, 2),
        ERROR("error", 10, 1),
        NOT_RUNNING("not_running", 0, 3);

        private final String name;
        @Getter
        private final int lightLevel;
        @Getter
        private final int uvOrder;

        RunningState(String name, int lightLevel, int uvOrder) {
            this.name = name;
            this.lightLevel = lightLevel;
            this.uvOrder = uvOrder;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
