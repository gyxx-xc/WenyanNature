package indi.wenyan.content.gui;

import indi.wenyan.setup.network.RunnerTextPacket;
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

    @Override
    protected boolean bookKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (owner.hasPermissions(2))
            return super.bookKeyPressed(keyCode, scanCode, modifiers);
        else
            return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (owner.hasPermissions(2))
            return super.charTyped(codePoint, modifiers);
        else
            return this.getFocused() != null && this.getFocused().charTyped(codePoint, modifiers);
    }
}
