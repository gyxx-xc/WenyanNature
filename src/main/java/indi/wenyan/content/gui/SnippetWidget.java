package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SnippetWidget extends AbstractScrollWidget {
    private final Font font;
    public SnippetWidget(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(font, "aaa",  mouseX, mouseY, 0xFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate" +
                ".editBox", getMessage(), "snippet"));
    }

    public int getInnerHeight() {
        return 1;
    }

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return 3;
    }

}
