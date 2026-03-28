package indi.wenyan.client.gui.behaviour;

import indi.wenyan.client.gui.code_editor.WritingEditorScreen;
import indi.wenyan.client.gui.code_editor.backend.WritingBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.WritingBackendSynchronizer;
import indi.wenyan.content.block.runner.ICodeHolder;
import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import indi.wenyan.judou.utils.function.ChineseUtils;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.server.WritingCodePacket;
import indi.wenyan.setup.network.server.WritingTitlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;

public enum WritingBlockBehaviour {
    ;

    public static void openGui(BlockPos pos, Player player) {
        if (!(player.level().getBlockEntity(pos) instanceof WritingBlockEntity entity)) return;
        ItemStack runners = entity.getItemStack();
        ICodeHolder code = runners.getCapability(WyRegistration.ITEM_CODE_HOLDER_CAPABILITY);
        if (code != null) {
            Minecraft.getInstance().setScreen(getWritingEditorScreen(pos, code));
        }
    }

    private static @NonNull WritingEditorScreen getWritingEditorScreen(BlockPos pos, ICodeHolder code) {
        return new WritingEditorScreen(new WritingBlockBackend(new WritingBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                code.setCode(content);
                ClientPacketDistributor.sendToServer(new WritingCodePacket(pos, content));
            }

            @Override
            public String getContent() {
                return code.getCode();
            }

            @Override
            public void sendTitle(String title) {
                var warppedTitle = ChineseUtils.bracketOf(title);
                code.setPlatformName(warppedTitle);
                ClientPacketDistributor.sendToServer(new WritingTitlePacket(pos, warppedTitle));
            }

            @Override
            public String getTitle() {
                var title = code.getPlatformName();

                if (title.length() < 2) {
                    return "";
                }
                return title.substring(1, title.length() - 1);
            }
        }));
    }
}
