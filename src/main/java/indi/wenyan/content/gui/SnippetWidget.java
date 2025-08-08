package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnippetWidget extends AbstractScrollWidget {
    private final Font font;
    private final CodeEditorWidget editor;

    public static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted"));
    //            new WidgetSprites(
//            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "widget/entry"),
//            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "widget/entry"),
//            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "widget/entry"));

    private static final Utils.BoxInformation buttonPadding =
        new Utils.BoxInformation(3, 3, 3, 3);
    public static final int ENTRY_HEIGHT = 10 + buttonPadding.vertical();

    public SnippetWidget(Font font, int x, int y, int width, int height, CodeEditorWidget editor) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.editor = editor;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int currentY = getY() + innerPadding();
        for (Snippet s : editor.getCurSnippets()) {
            if (withinContentAreaTopBottom(currentY, currentY + ENTRY_HEIGHT)) {
                boolean buttonHovered = mouseX >= getX() + innerPadding() &&
                        mouseX < getX() + getWidth() - innerPadding() &&
                        mouseY >= currentY && mouseY < currentY + ENTRY_HEIGHT;
                guiGraphics.blitSprite(SPRITES.get(this.active, buttonHovered),
                        getX() + innerPadding(), currentY,
                        this.getWidth() - totalInnerPadding(), ENTRY_HEIGHT);

                var text = Language.getInstance().getVisualOrder(
                        font.ellipsize(FormattedText.of(s.title()),
                                width - totalInnerPadding() - buttonPadding.horizontal()));
                guiGraphics.drawString(font, text,
                        getX() + innerPadding() + buttonPadding.left(), currentY + buttonPadding.top(),
                        0xFFFFFF, false);
            }
            currentY += ENTRY_HEIGHT;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            double x = mouseX - getX() - innerPadding();
            double y = mouseY - getY() - innerPadding() + scrollAmount();
            int index = Mth.floor(y / ENTRY_HEIGHT);
            if (index >= 0 && index < editor.getCurSnippets().size()) {
                if (x < width) {
                    editor.getTextField().insertSnippet(editor.getCurSnippets().get(index));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate" +
                ".editBox", getMessage(), "snippet"));
    }

    public int getInnerHeight() {
        return editor.getCurSnippets().size() * font.lineHeight;
    }

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return font.lineHeight;
    }
}
