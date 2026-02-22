package indi.wenyan.content.gui.code_editor.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class FuzhouNameWidget extends EditBox {

    private final Font font;
    private final int fullX;
    private final int fullY;
    private final int fullWidth;
    private static final String PREFIX = "符名: 「";
    private static final String SUFFIX = "」";

    public FuzhouNameWidget(Font font, int x, int y, int width, int height, Component message) {
        // Shift the actual EditBox to the right to make room for prefix
        // And shift down for bottom alignment
        super(font, x + font.width(PREFIX) + 4, y + 4, width - font.width(PREFIX) - 4, height, message);
        this.font = font;
        this.fullX = x;
        this.fullY = y;
        this.fullWidth = width;
        this.setBordered(false);
        this.setTextColor(0xFFFFFF); // White text matches usual Minecraft/Snippet style

        // Formatter only handles text, suffix is rendered manually
//        this.setFormatter((text, firstCharIndex) -> FormattedCharSequence.forward(text, Style.EMPTY));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render pure black background
        guiGraphics.fill(this.fullX, this.fullY,
                this.fullX + this.fullWidth, this.fullY + this.height,
                0xFF000000);

        // Render prefix "書「"
        guiGraphics.drawString(font, PREFIX, this.getX() - font.width(PREFIX), this.fullY + (this.height - 8) / 2 + 2,
                0xFFFFFF, false);

        // Render the EditBox content
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        // Render suffix "」" manually to ensure cursor is before it
        guiGraphics.drawString(font, SUFFIX, this.getX() + font.width(this.getValue()),
                this.fullY + (this.height - 8) / 2 + 2,
                0xFFFFFF, false);
    }
}
