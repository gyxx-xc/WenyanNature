package indi.wenyan.content.gui;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;

// from net.minecraft.client.gui.components.MultilineTextField;
@OnlyIn(Dist.CLIENT)
public class TextField {
    public static final int NO_CHARACTER_LIMIT = Integer.MAX_VALUE;
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private final List<StringView> displayLines = Lists.newArrayList();
    @Getter private String value = "";

    @Getter private int cursor = 0;
    private int selectCursor = 0;
    @Setter private boolean selecting;

    @Setter @Getter private int characterLimit = NO_CHARACTER_LIMIT;
    private final int width;

    @Setter private Consumer<String> valueListener = (s) -> {
    };
    @Setter private Runnable cursorListener = () -> {
    };

    public TextField(Font font, int width) {
        this.font = font;
        this.width = width;
        onValueChange();
    }


    public void setValue(String fullText) {
        value = hasCharacterLimit() ?
                StringUtil.truncateStringIfNecessary(fullText, characterLimit, false) : fullText;
        selectCursor = cursor = value.length();
        onValueChange();
    }

    public void insertText(String text) {
        if (!text.isEmpty() || hasSelection()) {
            String filteredText = StringUtil.filterText(text, true);
            String string = hasCharacterLimit() ?
                    StringUtil.truncateStringIfNecessary(filteredText, characterLimit - value.length(), false)
                    : filteredText;
            StringView stringView = getSelected();
            value = (new StringBuilder(value)).replace(stringView.beginIndex, stringView.endIndex, string).toString();
            cursor = stringView.beginIndex + string.length();
            selectCursor = cursor;
            onValueChange();
        }
    }

    public StringView getSelected() {
        return new StringView(Math.min(selectCursor, cursor), Math.max(selectCursor, cursor));
    }

    public int getLineAtCursor() {
        for (int i = 0; i < displayLines.size(); ++i) {
            StringView stringView = displayLines.get(i);
            if (cursor >= stringView.beginIndex && cursor <= stringView.endIndex) {
                return i;
            }
        }

        return -1;
    }

    public StringView getLineView(int lineNumber) {
        return displayLines.get(Mth.clamp(lineNumber, 0, displayLines.size() - 1));
    }

    public void seekCursorToPoint(double x, double y) {
        int i = Mth.floor(x);
        int j = Mth.floor(y / TextFieldScreen.LINE_HEIGHT);
        StringView stringView = displayLines.get(Mth.clamp(j, 0, displayLines.size() - 1));
        int k = font.plainSubstrByWidth(value.substring(stringView.beginIndex, stringView.endIndex), i).length();
        seekCursor(Whence.ABSOLUTE, stringView.beginIndex + k);
    }


    public boolean hasSelection() {
        return selectCursor != cursor;
    }

    public boolean hasCharacterLimit() {
        return characterLimit != NO_CHARACTER_LIMIT;
    }


    private String getSelectedText() {
        StringView stringView = getSelected();
        return value.substring(stringView.beginIndex, stringView.endIndex);
    }

    private StringView getCursorLineView() {
        return getCursorLineView(0);
    }

    private StringView getCursorLineView(int offset) {
        int i = getLineAtCursor();
        if (i < 0) {
            int var10002 = cursor;
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + value.length() + ")");
        } else {
            return displayLines.get(Mth.clamp(i + offset, 0, displayLines.size() - 1));
        }
    }

    private void onValueChange() {
        // reflowDisplayLines
        displayLines.clear();
        if (value.isEmpty()) {
            displayLines.add(StringView.EMPTY);
        } else {
            font.getSplitter().splitLines(value, width, Style.EMPTY, false,
                    (style, start, end) -> displayLines.add(new StringView(start, end)));
            if (value.charAt(value.length() - 1) == '\n') {
                displayLines.add(new StringView(value.length(), value.length()));
            }
        }

        valueListener.accept(value);
        cursorListener.run();
    }

    private void seekCursor(Whence whence, int position) {
        switch (whence) {
            case ABSOLUTE -> cursor = position;
            case RELATIVE -> cursor += position;
            case END -> cursor = value.length() + position;
        }

        cursor = Mth.clamp(cursor, 0, value.length());
        cursorListener.run();
        if (!selecting) {
            selectCursor = cursor;
        }

    }

    private void seekCursorLine(int offset) {
        if (offset != 0) {
            int i = font.width(value.substring(getCursorLineView().beginIndex, cursor)) + LINE_SEEK_PIXEL_BIAS;
            StringView cursorLineView = getCursorLineView(offset);
            int j = font.plainSubstrByWidth(value.substring(cursorLineView.beginIndex, cursorLineView.endIndex), i).length();
            seekCursor(Whence.ABSOLUTE, cursorLineView.beginIndex + j);
        }
    }

    private StringView getPreviousWord() {
        if (value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = Mth.clamp(cursor, 0, value.length() - 1);
            while (wordStart > 0 && Character.isWhitespace(value.charAt(wordStart - 1))) {
                wordStart--;
            }
            while (wordStart > 0 && !Character.isWhitespace(value.charAt(wordStart - 1))) {
                wordStart--;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private StringView getNextWord() {
        if (value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = getWordEndPosition(
                    Mth.clamp(cursor, 0, value.length() - 1));
            while (wordStart < value.length() &&
                    Character.isWhitespace(value.charAt(wordStart))) {
                wordStart++;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private int getWordEndPosition(int cursor) {
        int endCursor = cursor;
        while (endCursor < value.length() &&
                !Character.isWhitespace(value.charAt(endCursor))) {
            endCursor++;
        }
        return endCursor;
    }

    private void deleteText(int length) {
        if (!hasSelection())
            selectCursor = Mth.clamp(cursor + length, 0, value.length());
        insertText("");
    }

    public boolean keyPressed(int keyCode) {
        selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            cursor = value.length();
            selectCursor = 0;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
        } else if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
            insertText("");
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            insertText("\n");
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            seekCursor(Whence.ABSOLUTE, 0);
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            seekCursor(Whence.END, 0);
        } else if (Screen.hasControlDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> deleteText(getPreviousWord().beginIndex - cursor);
                case GLFW.GLFW_KEY_DELETE -> deleteText(getNextWord().beginIndex - cursor);
                case GLFW.GLFW_KEY_RIGHT -> seekCursor(Whence.ABSOLUTE, getNextWord().beginIndex);
                case GLFW.GLFW_KEY_LEFT -> seekCursor(Whence.ABSOLUTE, getPreviousWord().beginIndex);
                case GLFW.GLFW_KEY_HOME -> seekCursor(Whence.ABSOLUTE, 0);
                case GLFW.GLFW_KEY_END -> seekCursor(Whence.END, 0);
                default -> {
                    return false;
                }
            }
        } else {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> deleteText(-1);
                case GLFW.GLFW_KEY_DELETE -> deleteText(1);
                case GLFW.GLFW_KEY_RIGHT -> seekCursor(Whence.RELATIVE, 1);
                case GLFW.GLFW_KEY_LEFT -> seekCursor(Whence.RELATIVE, -1);
                case GLFW.GLFW_KEY_DOWN -> seekCursorLine(1);
                case GLFW.GLFW_KEY_UP -> seekCursorLine(-1);
                case GLFW.GLFW_KEY_HOME -> seekCursor(Whence.ABSOLUTE, getCursorLineView().beginIndex);
                case GLFW.GLFW_KEY_END -> seekCursor(Whence.ABSOLUTE, getCursorLineView().endIndex);
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }
}
