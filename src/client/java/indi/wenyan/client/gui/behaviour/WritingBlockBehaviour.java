package indi.wenyan.client.gui.behaviour;

import indi.wenyan.client.gui.code_editor.WritingEditorScreen;
import indi.wenyan.client.gui.code_editor.backend.WritingBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.interfaces.WritingBackendSynchronizer;
import indi.wenyan.content.block.writing_block.WritingBlockEntity;
import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.server.WritingCodePacket;
import indi.wenyan.setup.network.server.WritingTitlePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public enum WritingBlockBehaviour {;

    public static void openGui(BlockPos pos, Player player) {
        if (!(player.level().getBlockEntity(pos) instanceof WritingBlockEntity entity)) return;
        ItemStack runners = entity.getItemStack();
        Minecraft.getInstance().setScreen(new WritingEditorScreen(new WritingBlockBackend(new WritingBackendSynchronizer() {
            @Override
            public void sendContent(String content) {
                runners.set(WyRegistration.PROGRAM_CODE_DATA.get(), content);
                ClientPacketDistributor.sendToServer(new WritingCodePacket(pos, content));
            }

            @Override
            public String getContent() {
                return runners.getOrDefault(WyRegistration.PROGRAM_CODE_DATA.get(), "");
            }

            @Override
            public void sendTitle(String title) {
                var warppedTitle = Component.translatable("code.wenyan_programming.bracket", title);
                runners.set(DataComponents.CUSTOM_NAME, warppedTitle);
                ClientPacketDistributor.sendToServer(new WritingTitlePacket(pos, warppedTitle.getString()));

            }

            @Override
            public String getTitle() {
                var title = runners.getOrDefault(DataComponents.CUSTOM_NAME, Component.empty()).getString();

                if (title.length() < 2) {
                    return "";
                }
                return title.substring(1, title.length() - 1);
            }
        })));
    }
}
