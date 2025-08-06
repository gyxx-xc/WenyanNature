package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;

@SuppressWarnings("unused")
public class CodeEditorBuilder {
    private Font font;
    private int x;
    private int y;
    private int width;
    private int height;
    private String content;
    private int maxLength = StyledTextField.NO_CHARACTER_LIMIT;

    public CodeEditorBuilder font(Font font) {
        this.font = font;
        return this;
    }

    public CodeEditorBuilder position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public CodeEditorBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public CodeEditorBuilder content(String content) {
        this.content = content;
        return this;
    }

    public CodeEditorBuilder maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public CodeEditorWidget createTextFieldWidget() {
        return new CodeEditorWidget(font, x, y, width, height, maxLength, content);
    }
}