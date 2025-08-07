package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CodeEditorBuilder {
    private Font font;
    private int x;
    private int y;
    private int width;
    private int height;
    private String content;
    private int maxLength = CodeField.NO_CHARACTER_LIMIT;
    private Consumer<String> onChange = s -> {};

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

    public CodeEditorBuilder content(String content, Consumer<String> onChange) {
        this.content = content;
        this.onChange = onChange;
        return this;
    }

    public CodeEditorBuilder maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public CodeEditorWidget createTextFieldWidget() {
        return new CodeEditorWidget(font, x, y, width, height, maxLength, content, onChange);
    }
}