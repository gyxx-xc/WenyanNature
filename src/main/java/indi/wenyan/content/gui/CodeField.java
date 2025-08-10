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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

// from net.minecraft.client.gui.components.MultilineTextField;
@OnlyIn(Dist.CLIENT)
public class CodeField {
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private final List<StringView> displayLines = Lists.newArrayList();
    @Getter
    private final List<StyledView> styleMarks = Lists.newArrayList();

    public interface SavedVariable {
        List<Placeholder> getPlaceholders();
        StringBuilder getContent();
        int getCursor();
        void setCursor(int cursor);
        int getSelectCursor();
        void setSelectCursor(int selectCursor);
        boolean isSelecting();
        void setSelecting(boolean selecting);
    }
    private final SavedVariable screen;

    private final int width;

    @Setter private Consumer<String> valueListener = (s) -> {};
    @Setter private Runnable cursorListener = () -> {};
    @Setter private Supplier<Integer> widthUpdater = () -> 0;

    public CodeField(Font font, SavedVariable screen, int width) {
        this.font = font;
        this.screen = screen;
        this.width = width;
        onValueChange();
    }

    public void insertText(String text) {
        if (!text.isEmpty() || hasSelection()) {
            String filteredText = StringUtil.filterText(text.replace("\t", "    "), true);
            String string = StringUtil.truncateStringIfNecessary(filteredText,
                    CodeEditorScreen.CHARACTER_LIMIT - screen.getContent().length(), false);
            StringView stringView = getSelected();
            screen.getContent().replace(stringView.beginIndex(), stringView.endIndex(), string);
            int lengthChanged = string.length() - (stringView.endIndex() - stringView.beginIndex());
            ArrayList<Placeholder> placeholderArrayList = new ArrayList<>(screen.getPlaceholders());
            for (int i = 0; i < placeholderArrayList.size(); i++) {
                var placeholder = placeholderArrayList.get(i);
                // NOTE: a equal here means if any text of cursor is changed, the placeholder will be removed
                if (placeholder.index() >= stringView.beginIndex()) {
                    if (placeholder.index() <= stringView.endIndex())
                        screen.getPlaceholders().remove(placeholder);
                    else
                        screen.getPlaceholders().set(i, new Placeholder(
                                placeholder.context(), placeholder.index() + lengthChanged));
                }
            }
            screen.setCursor(stringView.beginIndex() + string.length());
            screen.setSelectCursor(screen.getCursor());
            onValueChange();
        }
    }

    public void insertSnippet(SnippetSet.Snippet snippet) {
        // get indent
        StringBuilder indent = new StringBuilder();
        if (screen.getCursor() > 0) {
            int lastNewline = screen.getContent().lastIndexOf("\n", screen.getCursor() - 1);
            int firstChar;
            if (lastNewline >= 0)
                firstChar = lastNewline + 1;
            else // first line
                firstChar = 0;
            while (firstChar < screen.getCursor() && Character.isWhitespace(screen.getContent().charAt(firstChar))) {
                indent.append(screen.getContent().charAt(firstChar));
                firstChar++;
            }
        }
        StringBuilder sb = new StringBuilder();
        int start = getSelected().beginIndex();
        List<String> lines = snippet.lines();
        // j for placeholders
        List<Placeholder> addPlaceholders = new ArrayList<>();
        for (int i = 0, j = 0; i < lines.size(); i++) {
            if (i > 0) sb.append(indent);
            while (j < snippet.insert().size() &&
                    snippet.insert().get(j).row() == i) {
                var p = snippet.insert().get(j++);
                addPlaceholders.add(new Placeholder(p.context(), start + sb.length() + p.colum()));
            }
            sb.append(lines.get(i));
            if (i != lines.size() - 1) sb.append('\n');
        }
        insertText(sb.toString());
        if (!addPlaceholders.isEmpty()) {
            screen.getPlaceholders().addAll(addPlaceholders);
            screen.getPlaceholders().sort(Comparator.comparing(Placeholder::index));
            screen.setSelectCursor(addPlaceholders.getFirst().index());
            screen.setCursor(addPlaceholders.getFirst().index());
            cursorListener.run(); // update cursor for placeholders
        } else {
            seekCursorNextPlaceholder();
        }
    }

    public StringView getSelected() {
        return new StringView(Math.min(screen.getSelectCursor(), screen.getCursor()), Math.max(screen.getSelectCursor(), screen.getCursor()));
    }

    public int getLineAtCursor() {
        for (int i = 0; i < displayLines.size(); ++i) {
            StringView stringView = displayLines.get(i);
            if (screen.getCursor() >= stringView.beginIndex() && screen.getCursor() <= stringView.endIndex()) {
                return i;
            }
        }

        return -1;
    }

    public void seekCursorToPoint(double x, double y) {
        int cursorY = Mth.floor(y / font.lineHeight);
        StringView stringView = displayLines.get(Mth.clamp(cursorY, 0, displayLines.size() - 1));
        String line = screen.getContent().substring(stringView.beginIndex(), stringView.endIndex());
        int cursorX = font.plainSubstrByWidth(line, Mth.floor(x)).length();
        if (cursorX < line.length()) {
            double inChar = x - font.width(line.substring(0, cursorX));
            cursorX += (inChar / font.width(line.substring(cursorX, cursorX + 1))) > 0.5 ? 1 : 0;
        }
        seekCursor(Whence.ABSOLUTE, stringView.beginIndex() + cursorX);
    }


    public boolean hasSelection() {
        return screen.getSelectCursor() != screen.getCursor();
    }

    private StringView getPreviousWord() {
        if (screen.getContent().isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = Mth.clamp(screen.getCursor(), 0, screen.getContent().length() - 1);
            while (wordStart > 0 && Character.isWhitespace(screen.getContent().charAt(wordStart - 1))) {
                wordStart--;
            }
            while (wordStart > 0 && !Character.isWhitespace(screen.getContent().charAt(wordStart - 1))) {
                wordStart--;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private StringView getNextWord() {
        if (screen.getContent().isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = getWordEndPosition(
                    Mth.clamp(screen.getCursor(), 0, screen.getContent().length() - 1));
            while (wordStart < screen.getContent().length() &&
                    Character.isWhitespace(screen.getContent().charAt(wordStart))) {
                wordStart++;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private int getWordEndPosition(int cursor) {
        int endCursor = cursor;
        while (endCursor < screen.getContent().length() &&
                !Character.isWhitespace(screen.getContent().charAt(endCursor))) {
            endCursor++;
        }
        return endCursor;
    }

    private StringView getCursorLineView(int offset) {
        int i = getLineAtCursor();
        if (i < 0) {
            int var10002 = screen.getCursor();
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + screen.getContent().length() + ")");
        } else {
            return displayLines.get(Mth.clamp(i + offset, 0, displayLines.size() - 1));
        }
    }

    private void seekCursor(Whence whence, int position) {
        switch (whence) {
            case ABSOLUTE -> screen.setCursor(position);
            case RELATIVE -> screen.setCursor(screen.getCursor() + position);
            case END -> screen.setCursor(screen.getContent().length() + position);
        }

        screen.setCursor(Mth.clamp(screen.getCursor(), 0, screen.getContent().length()));
        cursorListener.run();
        if (!screen.isSelecting()) {
            screen.setSelectCursor(screen.getCursor());
        }

    }

    private void seekCursorLine(int offset) {
        if (offset != 0) {
            int i = font.width(screen.getContent().substring(getCursorLineView(0).beginIndex(), screen.getCursor())) + LINE_SEEK_PIXEL_BIAS;
            StringView cursorLineView = getCursorLineView(offset);
            int j = font.plainSubstrByWidth(screen.getContent().substring(cursorLineView.beginIndex(), cursorLineView.endIndex()), i).length();
            seekCursor(Whence.ABSOLUTE, cursorLineView.beginIndex() + j);
        }
    }

    private boolean seekCursorNextPlaceholder() {
        Placeholder next = screen.getPlaceholders().stream()
                .filter(p -> p.index() > screen.getCursor())
                .min(Comparator.comparingInt(Placeholder::index))
                .orElse(null);
        if (next != null) {
            seekCursor(Whence.ABSOLUTE, next.index());
            return true;
        } else {
            return false;
        }
    }

    private void deleteText(int length) {
        if (!hasSelection())
            screen.setSelectCursor(Mth.clamp(screen.getCursor() + length, 0, screen.getContent().length()));
        insertText("");
    }

    public boolean keyPressed(int keyCode) {
        screen.setSelecting(Screen.hasShiftDown());
        if (Screen.isSelectAll(keyCode)) {
            screen.setCursor(screen.getContent().length());
            screen.setSelectCursor(0);
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(screen.getContent().substring(getSelected().beginIndex(), getSelected().endIndex()));
        } else if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(screen.getContent().substring(getSelected().beginIndex(), getSelected().endIndex()));
            insertText("");
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            insertText("\n");
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            seekCursor(Whence.ABSOLUTE, 0);
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            seekCursor(Whence.END, 0);
        } else if (Screen.hasControlDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> deleteText(getPreviousWord().beginIndex() - screen.getCursor());
                case GLFW.GLFW_KEY_DELETE -> deleteText(getNextWord().beginIndex() - screen.getCursor());
                case GLFW.GLFW_KEY_RIGHT -> seekCursor(Whence.ABSOLUTE, getNextWord().beginIndex());
                case GLFW.GLFW_KEY_LEFT -> seekCursor(Whence.ABSOLUTE, getPreviousWord().beginIndex());
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
                case GLFW.GLFW_KEY_HOME -> seekCursor(Whence.ABSOLUTE, getCursorLineView(0).beginIndex());
                case GLFW.GLFW_KEY_END -> seekCursor(Whence.ABSOLUTE, getCursorLineView(0).endIndex());
                case GLFW.GLFW_KEY_TAB -> {
                    // tab to next placeholder
                    if (!seekCursorNextPlaceholder())
                        insertText("    ");
                }
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
        int codeWidth = width - widthUpdater.get();
        if (screen.getContent().isEmpty()) {
            displayLines.add(StringView.EMPTY);
        } else {
            // split
            int lineStart = 0;
            int lineWidth = 0;
            for (int i = 0; i < screen.getContent().length(); i++) {
                char c = screen.getContent().charAt(i);
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
            if (lineStart < screen.getContent().length()) {
                displayLines.add(new StringView(lineStart, screen.getContent().length()));
            }

            if (screen.getContent().charAt(screen.getContent().length() - 1) == '\n') {
                displayLines.add(new StringView(screen.getContent().length(), screen.getContent().length()));
            }
        }

        if (screen.getContent().isEmpty()) {
            styleMarks.clear();
            styleMarks.add(new StyledView(0, -1));
        } else {
            var lexer = new WenyanRLexer(CharStreams.fromString(screen.getContent().toString()));
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
            styleMarks.add(new StyledView(screen.getContent().length(), -1));
        }

        valueListener.accept(screen.getContent().toString());
        cursorListener.run();
    }

    @OnlyIn(Dist.CLIENT)
    public record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public record StyledView(int endIndex, int style) {}

    @OnlyIn(Dist.CLIENT)
    public record Placeholder(Snippets.Context context, int index) {}
}
