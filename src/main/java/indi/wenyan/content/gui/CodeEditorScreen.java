package indi.wenyan.content.gui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CodeEditorScreen extends Screen implements CodeField.SavedVariable {
    private final Consumer<String> saving;

    // saved for code field
    @Getter
    private final StringBuilder content;
    @Getter
    private final List<CodeField.Placeholder> placeholders = new ArrayList<>();
    @Getter @Setter
    private int cursor = 0;
    @Getter @Setter
    private int selectCursor = 0;
    @Getter @Setter
    private boolean selecting = false;

    public static final int CHARACTER_LIMIT = 16384;

    private CodeEditorWidget textFieldWidget;
    @SuppressWarnings("FieldCanBeLocal")
    private SnippetWidget snippetWidget;

    public CodeEditorScreen(String content, Consumer<String> save) {
        super(Component.empty());
        this.content = new StringBuilder(
                StringUtil.truncateStringIfNecessary(content, CHARACTER_LIMIT, false));
        this.saving = save;
    }

    @Override
    protected void init() {
        int textFieldWidth = Mth.clamp(width/2, 50, CodeEditorWidget.WIDTH);
        textFieldWidget = new CodeEditorWidget(font, this,
                (width - textFieldWidth) / 2, 15,
                textFieldWidth, Math.min(height-30, CodeEditorWidget.HEIGH));
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

    // capture input to text field
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textFieldWidget.keyPressed(keyCode, scanCode, modifiers)) {
            setFocused(textFieldWidget);
            return true;
        } else if (snippetWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        setFocused(textFieldWidget);
        return textFieldWidget.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        saving.accept(content.toString());
        super.onClose();
    }
}
