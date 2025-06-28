package indi.wenyan.content.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TextFieldScreen extends Screen {
    private String data;
    private final Consumer<String> saving;

    public TextFieldScreen(@Nullable String s, Consumer<String> save) {
        super(Component.empty());
        this.data = s == null ? "" : s;
        this.saving = save;
    }

    @Override
    protected void init() {
        TextFieldWidget textFieldWidget = new TextFieldWidget(font, (width - 192) / 2 + 35, 15, 115, 159);
        textFieldWidget.setValue(data);
        textFieldWidget.setValueListener(s -> data = s);
        addRenderableWidget(textFieldWidget);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(BookViewScreen.BOOK_LOCATION, (width - 192) / 2, 2, 0, 0, 192, 192);
    }

    @Override
    public void onClose() {
        saving.accept(data);
        super.onClose();
    }
}
