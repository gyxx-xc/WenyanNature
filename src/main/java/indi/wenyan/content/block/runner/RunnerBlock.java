package indi.wenyan.content.block.runner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.gui.code_editor.RunnerBlockScreen;
import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditorBackendSynchronizer;
import indi.wenyan.client.gui.code_editor.widget.PackageSnippetWidget;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.definitions.WenyanItems;
import indi.wenyan.setup.network.BlockRunnerCodePacket;
import indi.wenyan.setup.network.PlatformRenamePacket;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

import static indi.wenyan.content.block.runner.RunnerBlockEntity.DEVICE_SEARCH_RANGE;

@ParametersAreNonnullByDefault
public class RunnerBlock extends AbstractFuluBlock implements EntityBlock {
    public static final EnumProperty<RunningState> RUNNING_STATE = EnumProperty.create("running_state",
            RunningState.class);
    @Getter
    private final int tier;

    public RunnerBlock(int tier, Properties properties) {
        super(properties.noCollision().lightLevel(state -> state.getValue(RUNNING_STATE).getLightLevel()));
        this.tier = tier;
        registerDefaultState(defaultBlockState()
                .setValue(RUNNING_STATE, RunningState.NOT_RUNNING));
    }

    public static final MapCodec<RunnerBlock> CODEC = RecordCodecBuilder.mapCodec(
            (i) -> i
                    .group(Codec.intRange(0, 6).fieldOf("tier").forGetter(RunnerBlock::getTier),
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
        if (player.isShiftKeyDown()) {
            if (level.isClientSide())
                openGui(runner, pos, level, player, state);
        } else {
            if (!level.isClientSide()) {
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

    // FIXME
    // @OnlyIn(Dist.CLIENT)
    private void openGui(RunnerBlockEntity runner, BlockPos pos, Level level, Player player, BlockState state) {
        List<PackageSnippetWidget.PackageSnippet> packageSnippets = new ArrayList<>();
        BlockPos attached = pos.relative(
                getConnectedDirection(state).getOpposite());
        if (level.getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            packageSnippets.add(packageSnippet(device.getExecPackage(),
                    device.blockState().getCloneItemStack(pos, level, false, player),
                    device.getPackageName()));

        for (BlockPos b : BlockPos.betweenClosed(
                pos.offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                pos.offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof IWenyanBlockDevice executor) {
                if (Objects.equals(executor.getPackageName(), "")) continue;
                RawHandlerPackage execPackage = executor.getExecPackage();
                packageSnippets.add(packageSnippet(execPackage,
                        executor.blockState().getCloneItemStack(pos, level, false, player),
                        executor.getPackageName()));
            } else if (blockEntity instanceof ICodeHolder entity && !b.equals(pos)) {
                packageSnippets.add(new PackageSnippetWidget.PackageSnippet(WenyanItems.HAND_RUNNER_1.toStack(),
                        entity.getPlatformName(), List.of()));
            }
        }
        Minecraft.getInstance().setScreen(new RunnerBlockScreen(getCodeEditorBackend(runner, pos, packageSnippets)));
    }

    // @OnlyIn(Dist.CLIENT)
    private static @NotNull RunnerBlockBackend getCodeEditorBackend(ICodeOutputHolder runner, BlockPos pos,
                                                                    List<PackageSnippetWidget.PackageSnippet> packageSnippets) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                runner.setCode(content);
                ClientPacketDistributor.sendToServer(new BlockRunnerCodePacket(pos, content));
            }

            @Override
            public String getContent() {
                return runner.getCode();
            }

            @Override
            public void sendTitle(String title) {
                String warppedTitle = Component.translatable("code.wenyan_programming.bracket", title).getString();
                runner.setPlatformName(warppedTitle);
                ClientPacketDistributor.sendToServer(new PlatformRenamePacket(pos, warppedTitle));
            }

            @Override
            public String getTitle() {
                var title = runner.getPlatformName();

                if (title.length() < 2) {
                    return "";
                }
                return title.substring(1, title.length() - 1);
            }

            @Override
            public Deque<Component> getOutput() {
                return runner.getOutputQueue();
            }

            @Override
            public boolean isOutputChanged() {
                return runner.isOutputChanged();
            }
        };
        return new RunnerBlockBackend(packageSnippets, synchronizer);
    }

    // @OnlyIn(Dist.CLIENT)
    private PackageSnippetWidget.PackageSnippet packageSnippet(RawHandlerPackage execPackage, ItemStack itemStack,
                                                               String name) {
        List<PackageSnippetWidget.Member> members = new ArrayList<>();
        execPackage.variables().forEach((k, v) -> {
            if (v.is(IWenyanObjectType.TYPE))
                members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.CLASS));
            else if (v.is(IWenyanFunction.TYPE))
                members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.METHOD));
            else
                members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.FIELD));
        });
        execPackage.functions().forEach((k, _) ->
                members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.METHOD)));
        return new PackageSnippetWidget.PackageSnippet(itemStack, name, members);
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
