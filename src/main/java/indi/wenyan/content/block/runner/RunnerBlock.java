package indi.wenyan.content.block.runner;

import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.gui.code_editor.CodeEditorBackend;
import indi.wenyan.content.gui.code_editor.CodeEditorBackendSynchronizer;
import indi.wenyan.content.gui.code_editor.CodeEditorScreen;
import indi.wenyan.content.gui.code_editor.PackageSnippetWidget;
import indi.wenyan.interpreter.exec_interface.HandlerPackageBuilder;
import indi.wenyan.interpreter.exec_interface.IWenyanBlockDevice;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.setup.Registration;
import indi.wenyan.setup.network.BlockRunnerCodePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static indi.wenyan.content.block.runner.RunnerBlockEntity.DEVICE_SEARCH_RANGE;

@ParametersAreNonnullByDefault
public class
RunnerBlock extends AbstractFuluBlock implements EntityBlock {
    public static final String ID = "runner_block";

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var runner = (RunnerBlockEntity) level.getBlockEntity(pos);
        assert runner != null;
        if (player.isShiftKeyDown()) {
            if (level.isClientSide())
                openGui(runner, pos, level, player, state);
        } else {
            if (!level.isClientSide()) {
                runner.playerRun(player);
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RunnerBlockEntity(blockPos, blockState);
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

    @OnlyIn(Dist.CLIENT)
    private void openGui(RunnerBlockEntity runner, BlockPos pos, Level level, Player player, BlockState state) {
        List<PackageSnippetWidget.PackageSnippet> packageSnippets = new ArrayList<>();
        BlockPos attached = pos.relative(
                getConnectedDirection(state).getOpposite());
        if (level.getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            packageSnippets.add(packageSnippet(device.getExecPackage(),
                    device.blockState().getCloneItemStack(new BlockHitResult(pos.getCenter(), Direction.UP, pos, false), level, pos, player),
                    device.getPackageName()));

        for (BlockPos b : BlockPos.betweenClosed(
                pos.offset(DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE),
                pos.offset(-DEVICE_SEARCH_RANGE, DEVICE_SEARCH_RANGE, -DEVICE_SEARCH_RANGE))) {
            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof IWenyanBlockDevice executor) {
                if (Objects.equals(executor.getPackageName(), "")) continue;
                HandlerPackageBuilder.RawHandlerPackage execPackage = executor.getExecPackage();
                packageSnippets.add(packageSnippet(execPackage,
                        executor.blockState().getCloneItemStack(new BlockHitResult(pos.getCenter(), Direction.UP, pos, false), level, pos, player),
                        executor.getPackageName()));
            } else if (blockEntity instanceof RunnerBlockEntity entity) {
                if (!b.equals(pos))
                    packageSnippets.add(new PackageSnippetWidget.PackageSnippet(Registration.HAND_RUNNER_1.toStack(), entity.getPlatformName(), List.of()));
            }
        }
        Minecraft.getInstance().setScreen(new CodeEditorScreen(getCodeEditorBackend(runner, pos, packageSnippets)));
    }

    private static @NotNull CodeEditorBackend getCodeEditorBackend(RunnerBlockEntity runner, BlockPos pos, List<PackageSnippetWidget.PackageSnippet> packageSnippets) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                runner.pages = content;
                runner.setChanged();
                PacketDistributor.sendToServer(new BlockRunnerCodePacket(pos, content));
            }

            @Override
            public String getContent() {
                return runner.pages;
            }

            @Override
            public void sendTitle(String title) {

            }

            @Override
            public String getTitle() {
                return "";
            }
        };
        var backend = new CodeEditorBackend(runner.pages, "", packageSnippets, synchronizer);
        return backend;
    }

    @OnlyIn(Dist.CLIENT)
    private PackageSnippetWidget.PackageSnippet packageSnippet(HandlerPackageBuilder.RawHandlerPackage execPackage, ItemStack itemStack, String name) {
        List<PackageSnippetWidget.Member> members = new ArrayList<>();
        execPackage.variables().forEach((k, v) -> {
                    if (v.is(IWenyanObjectType.TYPE))
                        members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.CLASS));
                    else if (v.is(IWenyanFunction.TYPE))
                        members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.METHOD));
                    else
                        members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.FIELD));
                }
        );
        execPackage.functions().forEach((k, v) ->
                members.add(new PackageSnippetWidget.Member(k, PackageSnippetWidget.MemberType.METHOD))
        );
        return new PackageSnippetWidget.PackageSnippet(itemStack, name, members);
    }
}
