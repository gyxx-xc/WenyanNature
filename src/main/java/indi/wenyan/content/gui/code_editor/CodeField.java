package indi.wenyan.content.gui.code_editor;

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

    private final CodeEditorBackend backend;

    private final int width;

    @Setter private Consumer<String> valueListener = (s) -> {};
    @Setter private Runnable cursorListener = () -> {};
    @Setter private Supplier<Integer> widthUpdater = () -> 0;

    public CodeField(Font font, CodeEditorBackend backend, int width) {
        this.font = font;
        this.backend = backend;
        this.width = width;
        onValueChange();
    }

    public void insertText(String text) {
        if (!text.isEmpty() || hasSelection()) {
            String filteredText = StringUtil.filterText(text.replace("\t", "    "), true);
            String string = StringUtil.truncateStringIfNecessary(filteredText,
                    CodeEditorScreen.CHARACTER_LIMIT - backend.getContent().length(), false);
            StringView stringView = getSelected();
            backend.getContent().replace(stringView.beginIndex(), stringView.endIndex(), string);
            int lengthChanged = string.length() - (stringView.endIndex() - stringView.beginIndex());
            for (int i = 0; i < backend.getPlaceholders().size(); i++) {
                var placeholder = backend.getPlaceholders().get(i);
                // NOTE: a equal here means if any text of cursor is changed, the placeholder will be removed
                if (placeholder.index() >= stringView.beginIndex()) {
                    if (placeholder.index() <= stringView.endIndex()) {
                        backend.getPlaceholders().remove(placeholder);
                        i --;
                    } else backend.getPlaceholders().set(i, new Placeholder(
                            placeholder.context(), placeholder.index() + lengthChanged));
                }
            }
            backend.setCursor(stringView.beginIndex() + string.length());
            backend.setSelectCursor(backend.getCursor());
            onValueChange();
        }
    }

    public void insertSnippet(SnippetSet.Snippet snippet) {
        // get indent
        StringBuilder indent = new StringBuilder();
        if (backend.getCursor() > 0) {
            int lastNewline = backend.getContent().lastIndexOf("\n", backend.getCursor() - 1);
            int firstChar;
            if (lastNewline >= 0)
                firstChar = lastNewline + 1;
            else // first line
                firstChar = 0;
            while (firstChar < backend.getCursor() && Character.isWhitespace(backend.getContent().charAt(firstChar))) {
                indent.append(backend.getContent().charAt(firstChar));
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
            backend.getPlaceholders().addAll(addPlaceholders);
            backend.getPlaceholders().sort(Comparator.comparing(Placeholder::index));
            backend.setSelectCursor(addPlaceholders.getFirst().index());
            backend.setCursor(addPlaceholders.getFirst().index());
            cursorListener.run();
            updateCurrentSnippetContext();
        } else {
            seekCursorNextPlaceholder();
        }
    }

    public StringView getSelected() {
        return new StringView(Math.min(backend.getSelectCursor(), backend.getCursor()), Math.max(backend.getSelectCursor(), backend.getCursor()));
    }

    public int getLineAtCursor() {
        for (int i = 0; i < displayLines.size(); ++i) {
            StringView stringView = displayLines.get(i);
            if (backend.getCursor() >= stringView.beginIndex() && backend.getCursor() <= stringView.endIndex()) {
                return i;
            }
        }

        return -1;
    }

    public void seekCursorToPoint(double x, double y) {
        int cursorY = Mth.floor(y / font.lineHeight);
        StringView stringView = displayLines.get(Mth.clamp(cursorY, 0, displayLines.size() - 1));
        String line = backend.getContent().substring(stringView.beginIndex(), stringView.endIndex());
        int cursorX = font.plainSubstrByWidth(line, Mth.floor(x)).length();
        if (cursorX < line.length()) {
            double inChar = x - font.width(line.substring(0, cursorX));
            cursorX += (inChar / font.width(line.substring(cursorX, cursorX + 1))) > 0.5 ? 1 : 0;
        }
        seekCursor(Whence.ABSOLUTE, stringView.beginIndex() + cursorX);
    }


    public boolean hasSelection() {
        return backend.getSelectCursor() != backend.getCursor();
    }

    private StringView getPreviousWord() {
        if (backend.getContent().isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = Mth.clamp(backend.getCursor(), 0, backend.getContent().length() - 1);
            while (wordStart > 0 && Character.isWhitespace(backend.getContent().charAt(wordStart - 1))) {
                wordStart--;
            }
            while (wordStart > 0 && !Character.isWhitespace(backend.getContent().charAt(wordStart - 1))) {
                wordStart--;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private StringView getNextWord() {
        if (backend.getContent().isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = getWordEndPosition(
                    Mth.clamp(backend.getCursor(), 0, backend.getContent().length() - 1));
            while (wordStart < backend.getContent().length() &&
                    Character.isWhitespace(backend.getContent().charAt(wordStart))) {
                wordStart++;
            }
            return new StringView(wordStart, getWordEndPosition(wordStart));
        }
    }

    private int getWordEndPosition(int cursor) {
        int endCursor = cursor;
        while (endCursor < backend.getContent().length() &&
                !Character.isWhitespace(backend.getContent().charAt(endCursor))) {
            endCursor++;
        }
        return endCursor;
    }

    private StringView getCursorLineView(int offset) {
        int i = getLineAtCursor();
        if (i < 0) {
            int var10002 = backend.getCursor();
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + backend.getContent().length() + ")");
        } else {
            return displayLines.get(Mth.clamp(i + offset, 0, displayLines.size() - 1));
        }
    }

    private void seekCursor(Whence whence, int position) {
        switch (whence) {
            case ABSOLUTE -> backend.setCursor(position);
            case RELATIVE -> backend.setCursor(backend.getCursor() + position);
            case END -> backend.setCursor(backend.getContent().length() + position);
        }

        backend.setCursor(Mth.clamp(backend.getCursor(), 0, backend.getContent().length()));
        cursorListener.run();
        updateCurrentSnippetContext();
        if (!backend.isSelecting()) {
            backend.setSelectCursor(backend.getCursor());
        }

    }

    private void seekCursorLine(int offset) {
        if (offset != 0) {
            int i = font.width(backend.getContent().substring(getCursorLineView(0).beginIndex(), backend.getCursor())) + LINE_SEEK_PIXEL_BIAS;
            StringView cursorLineView = getCursorLineView(offset);
            int j = font.plainSubstrByWidth(backend.getContent().substring(cursorLineView.beginIndex(), cursorLineView.endIndex()), i).length();
            seekCursor(Whence.ABSOLUTE, cursorLineView.beginIndex() + j);
        }
    }

    private boolean seekCursorNextPlaceholder() {
        Placeholder next = backend.getPlaceholders().stream()
                .filter(p -> p.index() > backend.getCursor())
                .min(Comparator.comparingInt(Placeholder::index))
                .orElse(null);
        if (next != null) {
            backend.setSelecting(false);
            seekCursor(Whence.ABSOLUTE, next.index());
            return true;
        } else {
            return false;
        }
    }

    private void deleteText(int length) {
        if (!hasSelection())
            backend.setSelectCursor(Mth.clamp(backend.getCursor() + length, 0, backend.getContent().length()));
        insertText("");
    }

    public boolean keyPressed(int keyCode) {
        backend.setSelecting(Screen.hasShiftDown());
        if (Screen.isSelectAll(keyCode)) {
            backend.setCursor(backend.getContent().length());
            backend.setSelectCursor(0);
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(backend.getContent().substring(getSelected().beginIndex(), getSelected().endIndex()));
        } else if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(backend.getContent().substring(getSelected().beginIndex(), getSelected().endIndex()));
            insertText("");
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            insertText("\n");
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            seekCursor(Whence.ABSOLUTE, 0);
        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            seekCursor(Whence.END, 0);
        } else if (Screen.hasControlDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> deleteText(getPreviousWord().beginIndex() - backend.getCursor());
                case GLFW.GLFW_KEY_DELETE -> deleteText(getNextWord().beginIndex() - backend.getCursor());
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
        if (backend.getContent().isEmpty()) {
            displayLines.add(StringView.EMPTY);
        } else {
            // split
            int lineStart = 0;
            int lineWidth = 0;
            for (int i = 0; i < backend.getContent().length(); i++) {
                char c = backend.getContent().charAt(i);
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
            if (lineStart < backend.getContent().length()) {
                displayLines.add(new StringView(lineStart, backend.getContent().length()));
            }

            if (backend.getContent().charAt(backend.getContent().length() - 1) == '\n') {
                displayLines.add(new StringView(backend.getContent().length(), backend.getContent().length()));
            }
        }

        if (backend.getContent().isEmpty()) {
            styleMarks.clear();
            styleMarks.add(new StyledView(0, -1));
        } else {
            var lexer = new WenyanRLexer(CharStreams.fromString(backend.getContent().toString()));
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
            styleMarks.add(new StyledView(backend.getContent().length(), -1));
        }

        valueListener.accept(backend.getContent().toString());
        cursorListener.run();
        updateCurrentSnippetContext();
    }

    private void updateCurrentSnippetContext() {
        int cursor = backend.getCursor();
        for (var placeholder : backend.getPlaceholders()) {
            if (cursor == placeholder.index()) {
                backend.getPlaceholders().remove(placeholder);
                backend.setCurSnippets(Snippets.getSnippets(placeholder.context()));
                return;
            }
        }
        backend.setCurSnippets(Snippets.DEFAULT_CONTEXT);
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
