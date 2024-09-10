package indi.wenyan.network;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ProgramTextServerPayloadHandler {
    public static void handleRunnerTextPacket(final RunnerTextPacket packet, final IPayloadContext context) {
        Player player = context.player();
        ItemStack runner = player.getInventory().items.get(packet.slot());
        runner.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(packet.pages().stream().map(Filterable::passThrough).toList()));
    }
}
