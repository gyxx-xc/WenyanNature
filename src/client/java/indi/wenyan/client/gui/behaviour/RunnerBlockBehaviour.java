package indi.wenyan.client.gui.behaviour;

import indi.wenyan.client.gui.code_editor.RunnerBlockScreen;
import indi.wenyan.client.gui.code_editor.RunnerDebugScreen;
import indi.wenyan.client.gui.code_editor.backend.PackageSnippet;
import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.RunnerDebugBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditorBackendSynchronizer;
import indi.wenyan.client.gui.code_editor.backend.interfaces.RunnerDebugSynchronizer;
import indi.wenyan.client.gui.code_editor.widget.PackageSnippetWidget;
import indi.wenyan.client.gui.llm.LLMRunnerBlockScreen;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.content.block.ICodeOutputHolder;
import indi.wenyan.content.block.cloud_beacon.GlobalPackageManager;
import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.ChineseUtils;
import indi.wenyan.judou.api.values.IWenyanFunction;
import indi.wenyan.judou.api.values.IWenyanObjectType;
import indi.wenyan.setup.config.WenyanConfig;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.server.BlockCodePacket;
import indi.wenyan.setup.network.server.BlockRenamePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

public enum RunnerBlockBehaviour {
    ;

    public static void openGui(BlockPos pos, Player player) {
        var level = player.level();
        if (!(level.getBlockEntity(pos) instanceof ICodeOutputHolder runner)) return;
        List<PackageSnippet> packageSnippets = getPackageSnippets(pos, player, level);
        Minecraft.getInstance().setScreen(new RunnerBlockScreen(getCodeEditorBackend(runner, pos, packageSnippets)));
    }

    private static @NonNull List<PackageSnippet> getPackageSnippets(BlockPos pos, Player player, Level level) {
        BlockState state = level.getBlockState(pos);
        List<PackageSnippet> packageSnippets = new ArrayList<>();
        Set<BlockPos> added = new HashSet<>();
        added.add(pos);

        BlockPos attached = pos.relative(
                AbstractFuluBlock.getConnectedDirection(state).getOpposite());
        var attachedExecutor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, attached);
        if (attachedExecutor != null) {
            packageSnippets.add(packageSnippet(attachedExecutor.getExecPackage(),
                    attachedExecutor.blockState().getCloneItemStack(pos, level, false, player),
                    attachedExecutor.getPackageName()));
            added.add(attached);
        }

        var manager = GlobalPackageManager.getInstance();
        var packagePos = manager.getAll();
        for (var entry : packagePos) {
            if (added.contains(entry)) continue;
            addSnippets(player, level, entry, packageSnippets, added);
        }

        int range = WenyanConfig.getRunnerRange();
        for (BlockPos b : BlockPos.betweenClosed(
                pos.offset(range, -range, range),
                pos.offset(-range, range, -range))) {
            addSnippets(player, level, b, packageSnippets, added);
        }
        return packageSnippets;
    }

    private static void addSnippets(Player player, Level level, BlockPos b, List<PackageSnippet> packageSnippets, Set<BlockPos> added) {
        if (added.contains(b)) return;

        BlockEntity blockEntity = level.getBlockEntity(b);
        if (blockEntity instanceof RunnerBlockEntity platform) {
            packageSnippets.add(new PackageSnippet(platform.getBlockState().getCloneItemStack(level, b, true),
                    platform.getPlatformName(), List.of()));
            added.add(b);
        }

        var executor = level.getCapability(WyRegistration.WENYAN_BLOCK_DEVICE_CAPABILITY, b);
        if (executor != null) {
            packageSnippets.add(packageSnippet(executor.getExecPackage(),
                    executor.blockState().getCloneItemStack(b, level, false, player),
                    executor.getPackageName()));
            added.add(b);
        }
    }

    private static @NotNull RunnerBlockBackend getCodeEditorBackend(ICodeOutputHolder runner, BlockPos pos,
                                                                    List<PackageSnippet> packageSnippets) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                runner.setCode(content);
                ClientPacketDistributor.sendToServer(new BlockCodePacket(pos, content));
            }


            @Override
            public String getContent() {
                return runner.getCode();
            }

            @Override
            public void sendTitle(String title) {
                String wrappedTitle = ChineseUtils.bracketOf(title);
                runner.setPlatformName(wrappedTitle);
                ClientPacketDistributor.sendToServer(new BlockRenamePacket(pos, wrappedTitle));
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

    private static @NotNull RunnerBlockBackend getCodeEditorBackendRo(ICodeOutputHolder runner,
                                                                      List<PackageSnippet> packageSnippets) {
        var synchronizer = new CodeEditorBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
//                runner.setCode(content);
//                ClientPacketDistributor.sendToServer(new BlockCodePacket(pos, content));
            }


            @Override
            public String getContent() {
                return runner.getCode();
            }

            @Override
            public void sendTitle(String title) {
//                String wrappedTitle = ChineseUtils.bracketOf(title);
//                runner.setPlatformName(wrappedTitle);
//                ClientPacketDistributor.sendToServer(new BlockRenamePacket(pos, wrappedTitle));
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
                members.add(new PackageSnippetWidget.Member(k, execPackage.metadata().get(k), PackageSnippetWidget.MemberType.CLASS));
            else if (v.is(IWenyanFunction.TYPE))
                members.add(new PackageSnippetWidget.Member(k, execPackage.metadata().get(k), PackageSnippetWidget.MemberType.METHOD));
            else
                members.add(new PackageSnippetWidget.Member(k, execPackage.metadata().get(k), PackageSnippetWidget.MemberType.FIELD));
        });
        execPackage.functions().forEach((k, _) ->
                members.add(new PackageSnippetWidget.Member(k, execPackage.metadata().get(k), PackageSnippetWidget.MemberType.METHOD)));
        return new PackageSnippet(itemStack, name, members);
    }

    public static void openDebugGui(BlockPos pos, Player player) {
        var level = player.level();
        if (!(level.getBlockEntity(pos) instanceof RunnerBlockEntity runner)) return;
        Minecraft.getInstance().setScreen(new RunnerDebugScreen(new RunnerDebugBackend(
                new RunnerDebugSynchronizer() {
                    @Override
                    public int getContextStart() {
                        return runner.getDebugContext().start();
                    }

                    @Override
                    public int getContextEnd() {
                        return runner.getDebugContext().end();
                    }

                    @Override
                    public Deque<Component> getOutput() {
                        return runner.getOutputQueue();
                    }

                    @Override
                    public boolean isOutputChanged() {
                        return runner.isOutputChanged();
                    }
                },
                runner.getCode(),
                runner.getOutputQueue()
        )));
    }

    public static void openLLMGui(BlockPos pos, Player player) {
        var level = player.level();
        if (!(level.getBlockEntity(pos) instanceof ICodeOutputHolder runner)) return;
        List<PackageSnippet> packageSnippets = getPackageSnippets(pos, player, level);
        Minecraft.getInstance().setScreen(new LLMRunnerBlockScreen(getCodeEditorBackend(runner, pos, packageSnippets)));
    }

    public static void openGuiRo(BlockPos pos, Player player) {
        var level = player.level();
        if (!(level.getBlockEntity(pos) instanceof ICodeOutputHolder runner)) return;
        List<PackageSnippet> packageSnippets = getPackageSnippets(pos, player, level);
        Minecraft.getInstance().setScreen(new RunnerBlockScreen(getCodeEditorBackendRo(runner, packageSnippets)));
    }
}
