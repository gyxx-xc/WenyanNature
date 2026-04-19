package indi.wenyan.client.gui.code_editor;

import indi.wenyan.client.gui.code_editor.backend.RunnerDebugBackend;
import indi.wenyan.client.gui.code_editor.widget.CodeEditorWidget;
import indi.wenyan.client.gui.code_editor.widget.CodeOutputWidget;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RunnerDebugScreen extends Screen {

    private final RunnerDebugBackend backend;

    @Getter
    private CodeEditorWidget textFieldWidget;
    @SuppressWarnings("FieldCanBeLocal")
    private CodeOutputWidget outputWindow;

    public RunnerDebugScreen(RunnerDebugBackend backend) {
        super(Component.empty());
        this.backend = backend;
    }

    @Override
    protected void init() {
        int titleBarHeight = 15;
        int textFieldWidth = Mth.clamp(width / 2, 50, CodeEditorWidget.WIDTH);
        int textFileHeight = Math.min(height - 30, CodeEditorWidget.HEIGH);
        textFieldWidget = new CodeEditorWidget(font, backend,
                (width - textFieldWidth) / 2, titleBarHeight,
                textFieldWidth, textFileHeight);
        addRenderableWidget(textFieldWidget);

        int outputWindowHeight = height - titleBarHeight - textFileHeight - 4;
        outputWindow = new CodeOutputWidget(
                (width - textFieldWidth) / 2, textFileHeight + titleBarHeight + 4,
                textFieldWidth, outputWindowHeight,
                Component.literal(""), font, backend);
        addRenderableWidget(outputWindow);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(guiGraphics);
    }

    @Override
    public void tick() {
        super.tick();
        backend.tick();
    }

    @Override
    public void onClose() {
        backend.save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
