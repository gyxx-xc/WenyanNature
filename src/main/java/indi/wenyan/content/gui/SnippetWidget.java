package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnippetWidget extends AbstractScrollWidget {
    private final Font font;
    private final CodeField textField;
    private final List<String> snippets = List.of("aa", "sss", "ee");

    public SnippetWidget(Font font, int x, int y, int width, int height, CodeField textField) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.textField = textField;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int currentY = getY() + innerPadding();
        for (String s : snippets) {
            if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                guiGraphics.drawString(font, s, getX() + innerPadding(), currentY, 0xFFFFFF, false);
            }
            currentY += font.lineHeight;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate" +
                ".editBox", getMessage(), "snippet"));
    }

    public int getInnerHeight() {
        return snippets.size() * font.lineHeight;
    }

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return font.lineHeight;
    }
}
