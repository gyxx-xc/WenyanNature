package indi.wenyan.content.gui;

import net.minecraft.client.gui.Font;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class TextFieldWidgetBuilder {
    private Font font;
    private int x;
    private int y;
    private int width;
    private int height;
    private String content;
    private Consumer<String> listener = s -> {};
    private int maxLength = TextField.NO_CHARACTER_LIMIT;

    public TextFieldWidgetBuilder font(Font font) {
        this.font = font;
        return this;
    }

    public TextFieldWidgetBuilder position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public TextFieldWidgetBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public TextFieldWidgetBuilder content(String content) {
        this.content = content;
        return this;
    }

    public TextFieldWidgetBuilder listen(Consumer<String> listener) {
        this.listener = listener;
        return this;
    }

    public TextFieldWidgetBuilder maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public TextFieldWidget createTextFieldWidget() {
        return new TextFieldWidget(font, x, y, width, height, maxLength, content, listener);
    }
}