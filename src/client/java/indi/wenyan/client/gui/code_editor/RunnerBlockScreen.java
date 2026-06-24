package indi.wenyan.client.gui.code_editor;

import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.widget.*;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class RunnerBlockScreen extends Screen {

    private final RunnerBlockBackend backend;

    public static final int CHARACTER_LIMIT = 16384;
    public static final int TITLE_LENGTH_LIMIT = 18;

    @Getter
    private CodeEditorWidget textFieldWidget;
    private SnippetWidget snippetWidget;
    private PackageSnippetWidget packageWidget;
    @SuppressWarnings("FieldCanBeLocal")
    private EditBox titleBar;
    @SuppressWarnings("FieldCanBeLocal")
    private CodeOutputWidget outputWindow;

    public RunnerBlockScreen(RunnerBlockBackend backend) {
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

        // -4 is spacing
        int snippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 140);
        snippetWidget = new SnippetWidget(font, backend,
                0, 15,
                snippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        snippetWidget.setResetFocus(() -> setFocused(textFieldWidget));
        addRenderableWidget(snippetWidget);

        int packageSnippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 280);
        packageWidget = new PackageSnippetWidget(font, backend,
                width - packageSnippetWidth, 15,
                packageSnippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        packageWidget.setResetFocus(() -> setFocused(textFieldWidget));
        addRenderableWidget(packageWidget);

        titleBar = new FuzhouNameWidget(font, snippetWidth + 4, 2,
                width - (snippetWidth + 4) - (packageSnippetWidth + 4), titleBarHeight,
                Component.literal(""), backend);
        titleBar.setTextColor(-1);
        titleBar.setBordered(false);
        titleBar.setMaxLength(18);
        addRenderableWidget(titleBar);

        int outputWindowHeight = height - titleBarHeight - textFileHeight - 4;
        outputWindow = new CodeOutputWidget(
                snippetWidth + 4, textFileHeight + titleBarHeight + 4,
                textFieldWidth, outputWindowHeight,
                Component.literal(""), font, backend);
        addRenderableWidget(outputWindow);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics,
                                   int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // tooltips
        snippetWidget.getTooltip().ifPresent(s -> SnippetWidget.renderSnippetTooltip(guiGraphics, font, mouseX, mouseY, s));
        packageWidget.getTooltip().ifPresent(m -> PackageSnippetWidget.renderSnippetTooltip(guiGraphics, font, mouseX, mouseY, m));
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(guiGraphics);
    }

    // HACK: mojang's mysterious code pass release and drag only for left button
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && this.isDragging()) {
            this.setDragging(false);
        }
        if (this.getFocused() != null) {
            return this.getFocused().mouseReleased(event);
        }

        return false;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        return this.getFocused() != null && this.getFocused().mouseDragged(event, dx, dy);
    }

    @Override
    public void tick() {
        super.tick();
        backend.tick();
        snippetWidget.tick();
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
