package indi.wenyan.content.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TextFieldScreen extends Screen {
    private final String data;
    private final Consumer<String> saving;

    private TextFieldWidget textFieldWidget;

    public TextFieldScreen(String data, Consumer<String> save) {
        super(Component.empty());
        this.data = data;
        this.saving = save;
    }

    @Override
    protected void init() {
        textFieldWidget = new TextFieldWidgetBuilder()
                .font(font).content(data)
                .position((width - TextFieldWidget.WIDTH) / 2, 15)
                .size(TextFieldWidget.WIDTH, TextFieldWidget.HEIGH)
                .createTextFieldWidget();
        addRenderableWidget(textFieldWidget);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
    }

    @Override
    public void onClose() {
        saving.accept(textFieldWidget.getValue());
        super.onClose();
    }
}
