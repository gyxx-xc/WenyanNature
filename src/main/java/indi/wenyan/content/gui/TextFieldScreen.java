package indi.wenyan.content.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextFieldScreen extends Screen {
    private TextFieldWidget textFieldWidget;
    String temp = "";

    public TextFieldScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        textFieldWidget = new TextFieldWidget(font, (width - 192) / 2+35, 15, 115, 159);
        textFieldWidget.setValue(temp);
        textFieldWidget.setValueListener(s -> temp = s);
        addRenderableWidget(textFieldWidget);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(BookViewScreen.BOOK_LOCATION, (width - 192) / 2, 2, 0, 0, 192, 192);
    }
}
