package indi.wenyan.content.gui;

import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CodeEditScreen extends Screen {
    @Setter
    private String data;
    private final Consumer<String> saving;

    // saved for code field
    private final List<CodeField.Placeholder> placeholders = new ArrayList<>();

    private CodeEditorWidget textFieldWidget;
    @SuppressWarnings("FieldCanBeLocal")
    private SnippetWidget snippetWidget;

    public CodeEditScreen(String data, Consumer<String> save) {
        super(Component.empty());
        this.data = data;
        this.saving = save;
    }

    @Override
    protected void init() {
        int textFieldWidth = Mth.clamp(width/2, 50, CodeEditorWidget.WIDTH);
        textFieldWidget = new CodeEditorBuilder()
                // placeholders is pointer
                .font(font).content(data, this::setData).placeholders(placeholders)
                .position((width - textFieldWidth) / 2, 15)
                .size(textFieldWidth, Math.min(height-30, CodeEditorWidget.HEIGH))
                .maxLength(16384)
                .create();
        addRenderableWidget(textFieldWidget);

        int snippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 140);
        snippetWidget = new SnippetWidget(font,
                width - snippetWidth, 15,
                snippetWidth, Math.min(height-30, CodeEditorWidget.HEIGH),
                textFieldWidget);
        addRenderableWidget(snippetWidget);
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
