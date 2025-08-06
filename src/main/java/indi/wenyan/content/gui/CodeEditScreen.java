package indi.wenyan.content.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CodeEditScreen extends Screen {
    private final String data;
    private final Consumer<String> saving;

    private CodeEditorWidget textFieldWidget;

    public CodeEditScreen(String data, Consumer<String> save) {
        super(Component.empty());
        this.data = data;
        this.saving = save;
    }

    @Override
    protected void init() {
        textFieldWidget = new CodeEditorBuilder()
                .font(font).content(data)
                .position((width - CodeEditorWidget.WIDTH) / 2, 15)
                .size(CodeEditorWidget.WIDTH, CodeEditorWidget.HEIGH)
                .maxLength(16384)
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
