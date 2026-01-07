package indi.wenyan.content.gui.code_editor;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
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
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // tooltips
        snippetWidget.getRenderingSnippetTooltip().ifPresent(s ->
                renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
    }

    public void renderSnippetTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY,
                                     SnippetSet.Snippet snippet) {
        List<Component> tooltip = Lists.newArrayList();
        tooltip.add(Component.literal(snippet.title()));
        if (!hasShiftDown()) {
            tooltip.add(Component.translatable("gui.wenyan.hold_shift").withStyle(ChatFormatting.GRAY));
        } else {
            int curInsert = 0;
            for (int row = 0; row < snippet.lines().size(); row++) {
                String line = snippet.lines().get(row);
                int curColum = 0;
                MutableComponent lineComp = Component.literal("");
                while (curInsert < snippet.insert().size() &&
                        snippet.insert().get(curInsert).row() == row) {
                    var placeholder = snippet.insert().get(curInsert++);

                    Component textComp = Component.literal(line.substring(curColum,
                            placeholder.colum())).withStyle(ChatFormatting.GRAY);

                    Component placeholderComp = Component.literal(placeholder.context().value())
                            .withStyle(Style.EMPTY.withColor(Snippets.contextColor(placeholder.context())));
                    System.out.println(placeholderComp);
                    lineComp.append(textComp).append(placeholderComp);
                    curColum = placeholder.colum();
                }
                Component textComp = Component.literal(line.substring(curColum))
                        .withStyle(ChatFormatting.GRAY);
                lineComp.append(textComp);
                tooltip.add(lineComp);
            }
        }
        guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
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
