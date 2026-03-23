package indi.wenyan.client.gui.behaviour;

import indi.wenyan.client.gui.code_editor.RunnerBlockScreen;
import indi.wenyan.client.gui.code_editor.backend.PackageSnippet;
import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditorBackendSynchronizer;
import indi.wenyan.client.gui.code_editor.widget.PackageSnippetWidget;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.runner.ICodeOutputHolder;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.interpreter_impl.IWenyanBlockDevice;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.judou.utils.ChineseUtils;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.server.BlockRunnerCodePacket;
import indi.wenyan.setup.network.server.PlatformRenamePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public enum RunnerBlockBehaviour {
    ;

    public static void openGui(BlockPos pos, Player player) {
        var level = player.level();
        BlockState state = level.getBlockState(pos);
        if (!(level.getBlockEntity(pos) instanceof ICodeOutputHolder runner)) return;
        List<PackageSnippet> packageSnippets = new ArrayList<>();
        BlockPos attached = pos.relative(
                AbstractFuluBlock.getConnectedDirection(state).getOpposite());
        if (level.getBlockEntity(attached) instanceof IWenyanBlockDevice device)
            packageSnippets.add(packageSnippet(device.getExecPackage(),
                    device.blockState().getCloneItemStack(pos, level, false, player),
                    device.getPackageName()));

        int range = WenyanConfig.getRunnerRange();
        for (BlockPos b : BlockPos.betweenClosed(
                pos.offset(range, -range, range),
                pos.offset(-range, range, -range))) {
            if (b.equals(pos)) continue;

            BlockEntity blockEntity = level.getBlockEntity(b);
            if (blockEntity instanceof RunnerBlockEntity platform) {
                packageSnippets.add(new PackageSnippet(platform.getBlockState().getCloneItemStack(level, b, true),
                        platform.getPlatformName(), List.of()));
            }

            var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, b);
            if (executor != null) {
                packageSnippets.add(packageSnippet(executor.getExecPackage(),
                        executor.blockState().getCloneItemStack(b, level, false, player),
                        executor.getPackageName()));
            }
        }
        Minecraft.getInstance().setScreen(new RunnerBlockScreen(getCodeEditorBackend(runner, pos, packageSnippets)));
    }

    private static @NotNull RunnerBlockBackend getCodeEditorBackend(ICodeOutputHolder runner, BlockPos pos,
                                                                    List<PackageSnippet> packageSnippets) {
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
                String warppedTitle = ChineseUtils.bracketOf(title);
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

    private static PackageSnippet packageSnippet(RawHandlerPackage execPackage, ItemStack itemStack,
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
        return new PackageSnippet(itemStack, name, members);
    }
}
