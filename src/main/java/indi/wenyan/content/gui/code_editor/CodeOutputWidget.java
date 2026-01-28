package indi.wenyan.content.gui.code_editor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.network.chat.Component;

public class CodeOutputWidget extends FittingMultiLineTextWidget {
    public CodeOutputWidget(int x, int y, int width, int height, Component message, Font font) {
        super(x, y, width, height, message, font);
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFFFFFFFF);
    }
}
