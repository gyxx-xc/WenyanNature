package indi.wenyan.client.gui.code_editor.widget;

import indi.wenyan.client.gui.code_editor.backend.interfaces.TitleBackend;
import indi.wenyan.setup.language.GuiText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class FuzhouNameWidget extends EditBox {

    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int FOREGROUND_COLOR = 0xFFFFFFFF;

    private final Font font;
    private final int fullX;
    private final int fullY;
    private final int fullWidth;
    private static final String PREFIX = "「";
    private static final String SUFFIX = "」";

    public FuzhouNameWidget(Font font, int x, int y, int width, int height, Component message, TitleBackend backend) {
        // Shift the actual EditBox to the right to make room for prefix
        // And shift down for bottom alignment
        final int promptLength = font.width(GuiText.FuNamePrompt.text()) + font.width(PREFIX);
        super(font, x + promptLength + 4, y + 4, width - promptLength, height, message);
        this.font = font;
        this.fullX = x;
        this.fullY = y;
        this.fullWidth = width;
        this.setBordered(false);
        this.setTextColor(FOREGROUND_COLOR); // White text matches usual Minecraft/Snippet style
        setValue(backend.getTitle());
        setResponder(backend::setTitle);

        // Formatter only handles text, suffix is rendered manually
//        this.setFormatter((text, firstCharIndex) -> FormattedCharSequence.forward(text, Style.EMPTY));
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render pure black background
        guiGraphics.fill(this.fullX, this.fullY,
                this.fullX + this.fullWidth, this.fullY + this.height,
                BACKGROUND_COLOR);

        final int promptLength = font.width(GuiText.FuNamePrompt.text()) + font.width(PREFIX);
        // Render prefix "書「"
        guiGraphics.text(font, GuiText.FuNamePrompt.text().append(PREFIX),
                this.getX() - promptLength - 4, this.getY(),
                FOREGROUND_COLOR, false);

        // Render the EditBox content
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // Render suffix "」" manually to ensure cursor is before it
        guiGraphics.text(font, SUFFIX, this.getX() + font.width(this.getValue()) + 4,
                this.getY(),
                FOREGROUND_COLOR, false);
    }
}
