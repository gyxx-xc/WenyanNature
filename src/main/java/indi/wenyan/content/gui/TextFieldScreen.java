package indi.wenyan.content.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextFieldScreen extends Screen {
    public TextFieldScreen(Component title) {
        super(title);
    }

    protected TextField createCommentBox(int width, int height, Consumer<String> valueListener) {
        TextField textField = new TextField(this.font, (this.width - 192) / 2, 2, width, height, Component.literal(""), Component.empty());
        textField.setValueListener(valueListener);
        return textField;
    }

    @Override
    protected void init() {
        addRenderableWidget(new TextField(this.font, (this.width - 192) / 2+35, 15, 115, 159, Component.literal(""), Component.empty()));
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blit(BookViewScreen.BOOK_LOCATION, (this.width - 192) / 2, 2, 0, 0, 192, 192);
    }
}
