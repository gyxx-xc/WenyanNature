package indi.wenyan.gui;

import indi.wenyan.network.RunnerTextPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.neoforged.neoforge.network.PacketDistributor;

public class HandRunnerScreen extends RunnerScreen {
    private final Player owner;
    private final ItemStack book;
    private final InteractionHand hand;

    public HandRunnerScreen(Player owner, ItemStack book, InteractionHand hand) {
        super(book.get(DataComponents.WRITABLE_BOOK_CONTENT));
        this.owner = owner;
        this.book = book;
        this.hand = hand;
    }

    @Override
    protected void saveChanges() {
        // local
        this.book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(this.pages.stream().map(Filterable::passThrough).toList()));
        // remote
        int slot = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
        PacketDistributor.sendToServer(new RunnerTextPacket(slot, this.pages));
    }
}
