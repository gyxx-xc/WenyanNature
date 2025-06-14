package indi.wenyan.setup.network;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public class ProgramTextServerPayloadHandler implements IPayloadHandler<RunnerTextPacket> {
    @Override
    public void handle(@NotNull RunnerTextPacket payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            ItemStack runner = player.getInventory().items.get(payload.slot());
            runner.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(payload.pages().stream().map(Filterable::passThrough).toList()));
        }
    }
}
