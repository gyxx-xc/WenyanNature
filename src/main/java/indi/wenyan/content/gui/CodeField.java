package indi.wenyan.content.gui;

import com.google.common.collect.Lists;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

// from net.minecraft.client.gui.components.MultilineTextField;
@OnlyIn(Dist.CLIENT)
public class CodeField {
    public static final int NO_CHARACTER_LIMIT = Integer.MAX_VALUE;
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private final List<StringView> displayLines = Lists.newArrayList();
    @Getter
    private final List<StyledView> styleMarks = Lists.newArrayList();
    @Getter private String value = "";

    @Getter private int cursor = 0;
    private int selectCursor = 0;
    @Setter private boolean selecting;

    @Setter @Getter private int characterLimit = NO_CHARACTER_LIMIT;
    private final int width;

    @Setter private Consumer<String> valueListener = (s) -> {};
    @Setter private Runnable cursorListener = () -> {};
    @Setter private Function<String, Integer> widthUpdater = (s) -> 0;

    public CodeField(Font font, int width) {
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
            String filteredText = StringUtil.filterText(text.replace("\t", "    "), true);
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

    public void insertSnippet(Snippet snippet) {
        // get indent
        StringBuilder indent = new StringBuilder();
        if (cursor > 0 && cursor < value.length()) {
            int lastNewline = value.lastIndexOf('\n', cursor - 1);
            int firstChar;
            if (lastNewline >= 0)
                firstChar = lastNewline + 1;
            else // first line
                firstChar = 0;
            while (firstChar < cursor - 1 && Character.isWhitespace(value.charAt(firstChar))) {
                indent.append(value.charAt(firstChar));
                firstChar++;
            }
        }
        StringBuilder sb = new StringBuilder();
        List<String> lines = snippet.lines();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append(indent);
            sb.append(lines.get(i));
            if (i != lines.size() - 1) sb.append('\n');
        }
        insertText(sb.toString());
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

    public void seekCursorToPoint(double x, double y) {
        int cursorY = Mth.floor(y / font.lineHeight);
        StringView stringView = displayLines.get(Mth.clamp(cursorY, 0, displayLines.size() - 1));
        String line = value.substring(stringView.beginIndex, stringView.endIndex);
        int cursorX = font.plainSubstrByWidth(line, Mth.floor(x)).length();
        if (cursorX < line.length()) {
            double inChar = x - font.width(line.substring(0, cursorX));
            cursorX += (inChar / font.width(line.substring(cursorX, cursorX + 1))) > 0.5 ? 1 : 0;
        }
        seekCursor(Whence.ABSOLUTE, stringView.beginIndex + cursorX);
    }


    public boolean hasSelection() {
        return selectCursor != cursor;
    }

    public boolean hasCharacterLimit() {
        return characterLimit != NO_CHARACTER_LIMIT;
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

    private StringView getCursorLineView(int offset) {
        int i = getLineAtCursor();
        if (i < 0) {
            int var10002 = cursor;
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + value.length() + ")");
        } else {
            return displayLines.get(Mth.clamp(i + offset, 0, displayLines.size() - 1));
        }
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
            int i = font.width(value.substring(getCursorLineView(0).beginIndex, cursor)) + LINE_SEEK_PIXEL_BIAS;
            StringView cursorLineView = getCursorLineView(offset);
            int j = font.plainSubstrByWidth(value.substring(cursorLineView.beginIndex, cursorLineView.endIndex), i).length();
            seekCursor(Whence.ABSOLUTE, cursorLineView.beginIndex + j);
        }
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
            Minecraft.getInstance().keyboardHandler.setClipboard(value.substring(getSelected().beginIndex, getSelected().endIndex));
        } else if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(value.substring(getSelected().beginIndex, getSelected().endIndex));
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
                case GLFW.GLFW_KEY_HOME -> seekCursor(Whence.ABSOLUTE, getCursorLineView(0).beginIndex);
                case GLFW.GLFW_KEY_END -> seekCursor(Whence.ABSOLUTE, getCursorLineView(0).endIndex);
                default -> {
                    return false;
                }
            }
        }
        return true;
    }


    private void onValueChange() {
        // reflowDisplayLines
        displayLines.clear();
        int codeWidth = width - widthUpdater.apply(value);
        if (value.isEmpty()) {
            displayLines.add(StringView.EMPTY);
        } else {
            // split
            int lineStart = 0;
            int lineWidth = 0;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '\n') {
                    displayLines.add(new StringView(lineStart, i));
                    lineStart = i + 1;
                    lineWidth = 0;
                    continue;
                }
                int charWidth = font.width(String.valueOf(c));
                if (lineWidth + charWidth > codeWidth) {
                    displayLines.add(new StringView(lineStart, i));
                    lineStart = i;
                    lineWidth = charWidth;
                } else {
                    lineWidth += charWidth;
                }
            }
            if (lineStart < value.length()) {
                displayLines.add(new StringView(lineStart, value.length()));
            }

            if (value.charAt(value.length() - 1) == '\n') {
                displayLines.add(new StringView(value.length(), value.length()));
            }
        }

        if (value.isEmpty()) {
            styleMarks.clear();
            styleMarks.add(new StyledView(0, -1));
        } else {
            var lexer = new WenyanRLexer(CharStreams.fromString(value));
            lexer.removeErrorListeners();
            var token = new CommonTokenStream(lexer);
            token.fill();
            styleMarks.clear();
            int lastIndex = 0;
            for (var t : token.getTokens()) {
                if (t.getStartIndex() > lastIndex) {
                    styleMarks.add(new StyledView(t.getStartIndex()+1, -1));
                }
                if (t.getType() == WenyanRLexer.EOF) { break; }
                styleMarks.add(new StyledView(t.getStopIndex()+1, t.getType()));
                lastIndex = t.getStopIndex() + 1;
            }
            styleMarks.add(new StyledView(value.length(), -1));
        }

        valueListener.accept(value);
        cursorListener.run();
    }

    @OnlyIn(Dist.CLIENT)
    public record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public record StyledView(int endIndex, int style) {}
}
