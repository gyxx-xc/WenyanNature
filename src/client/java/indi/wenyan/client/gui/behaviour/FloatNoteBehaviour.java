package indi.wenyan.client.gui.behaviour;

import indi.wenyan.client.gui.float_note.FloatNoteNamingScreen;
import indi.wenyan.content.block.IRenamable;
import indi.wenyan.setup.network.server.BlockRenamePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.Consumer;

public enum FloatNoteBehaviour {
    ;

    public static void openGui(BlockPos pos, InteractionHand hand, Player player) {
        ItemStack item = player.getItemInHand(hand);
        Consumer<String> setNameFunc = name -> {
            if (player.level().getBlockEntity(pos) instanceof IRenamable renamable)
                renamable.setName(name);
            ClientPacketDistributor.sendToServer(new BlockRenamePacket(pos, name));
        };
        Minecraft.getInstance().setScreen(new FloatNoteNamingScreen(setNameFunc, item));
    }
}
