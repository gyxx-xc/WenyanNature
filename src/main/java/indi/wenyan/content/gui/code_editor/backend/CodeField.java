package indi.wenyan.content.gui.code_editor.backend;

import com.google.common.collect.Lists;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Recognizer;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

// copy from net.minecraft.client.gui.components.MultilineTextField;
@OnlyIn(Dist.CLIENT)
public class CodeField {
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private final List<StringView> displayLines = Lists.newArrayList();
    @Getter
    private final List<StyledView> styleMarks = Lists.newArrayList();

    @Setter
    boolean selecting = false;

    private final CodeEditorBackend backend;

    private final Supplier<Integer> widthUpdater;

    public CodeField(Font font, CodeEditorBackend backend,
                     Supplier<Integer> widthUpdater,
                     Runnable cursorListener
                     ) {
        this.font = font;
        this.backend = backend;
        backend.setValueListener(this::onValueChange);
        backend.setCursorListener(() -> {
            cursorListener.run();
            updateCurrentSnippetContext();
        });
        this.widthUpdater = widthUpdater;
        onValueChange();
    }

    public void insertText(String text) {
        backend.insertText(text);
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
        int tempCursor = switch (whence) {
            case ABSOLUTE -> position;
            case RELATIVE -> backend.getCursor() + position;
            case END -> backend.getContent().length() + position;
        };

        backend.setCursor(Mth.clamp(tempCursor, 0, backend.getContent().length()));
        if (!selecting) {
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
            selecting = false;
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
        selecting = Screen.hasShiftDown();
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
        int codeWidth = widthUpdater.get();
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
            var lexer = new WenyanRLexer(CharStreams.fromString(backend.getContent()));
            lexer.removeErrorListeners();
            var token = new CommonTokenStream(lexer);
            token.fill();
            styleMarks.clear();
            int lastIndex = 0;
            for (var t : token.getTokens()) {
                if (t.getStartIndex() > lastIndex) {
                    styleMarks.add(new StyledView(t.getStartIndex()+1, -1));
                }
                if (t.getType() == Recognizer.EOF) { break; }
                styleMarks.add(new StyledView(t.getStopIndex()+1, t.getType()));
                lastIndex = t.getStopIndex() + 1;
            }
            styleMarks.add(new StyledView(backend.getContent().length(), -1));
        }
    }

    private void updateCurrentSnippetContext() {
        int cursor = backend.getCursor();
        for (var placeholder : backend.getPlaceholders()) {
            if (cursor == placeholder.index()) {
                backend.getPlaceholders().remove(placeholder);
                backend.setCurSnippets(generated_Snippets.getSnippets(placeholder.context()));
                return;
            }
        }
        backend.setCurSnippets(generated_Snippets.DEFAULT_CONTEXT);
    }


    @OnlyIn(Dist.CLIENT)
    public record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public record StyledView(int endIndex, int style) {}

    @OnlyIn(Dist.CLIENT)
    public record Placeholder(generated_Snippets.Context context, int index) {}
}
