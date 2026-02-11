package indi.wenyan.content.gui.code_editor.backend;

import com.google.common.collect.Lists;
import indi.wenyan.judou.antlr.WenyanRLexer;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

// copy from net.minecraft.client.gui.components.MultilineTextField
@OnlyIn(Dist.CLIENT)
public class CodeField {
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private final List<StyledLineView> displayLines = Lists.newArrayList();
    @Getter
    private final List<StyledStringView> styleMarks = Lists.newArrayList();

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
            StyledLineView stringView = displayLines.get(i);
            if (backend.getCursor() >= stringView.beginIndex() && backend.getCursor() <= stringView.endIndex()) {
                return i;
            }
        }

        return -1;
    }

    public void seekCursorToPoint(double x, double y) {
        int cursorY = Mth.floor(y / font.lineHeight);
        StyledLineView stringView = displayLines.get(Mth.clamp(cursorY, 0, displayLines.size() - 1));
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
            return displayLines.get(Mth.clamp(i + offset, 0, displayLines.size() - 1)).stringView();
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
        if (handleScreenDefined(keyCode))
            return true;
        else if (handleNoModifiers(keyCode))
            return true;
        else if (Screen.hasControlDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE ->
                        deleteText(getPreviousWord().beginIndex() - backend.getCursor());
                case GLFW.GLFW_KEY_DELETE ->
                        deleteText(getNextWord().beginIndex() - backend.getCursor());
                case GLFW.GLFW_KEY_RIGHT -> seekCursor(Whence.ABSOLUTE, getNextWord().beginIndex());
                case GLFW.GLFW_KEY_LEFT ->
                        seekCursor(Whence.ABSOLUTE, getPreviousWord().beginIndex());
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
                case GLFW.GLFW_KEY_HOME ->
                        seekCursor(Whence.ABSOLUTE, getCursorLineView(0).beginIndex());
                case GLFW.GLFW_KEY_END ->
                        seekCursor(Whence.ABSOLUTE, getCursorLineView(0).endIndex());
                case GLFW.GLFW_KEY_TAB -> {
                    // tab to next placeholder
                    boolean success = seekCursorNextPlaceholder();
                    if (!success) insertText("    ");
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean handleNoModifiers(int keyCode) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                insertText("\n");
                return true;
            }
            case GLFW.GLFW_KEY_PAGE_UP -> {
                seekCursor(Whence.ABSOLUTE, 0);
                return true;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN -> {
                seekCursor(Whence.END, 0);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean handleScreenDefined(int keyCode) {
        if (Screen.isSelectAll(keyCode)) {
            backend.setCursor(backend.getContent().length());
            backend.setSelectCursor(0);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(
                    backend.getContent()
                            .substring(getSelected().beginIndex(), getSelected().endIndex()));
            return true;
        } else if (Screen.isPaste(keyCode)) {
            insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(
                    backend.getContent()
                            .substring(getSelected().beginIndex(), getSelected().endIndex()));
            insertText("");
            return true;
        } else {
            return false;
        }
    }

    private void onValueChange() {
        displayLines.clear();
        int codeWidth = widthUpdater.get();
        if (backend.getContent().isEmpty()) {
            displayLines.add(StyledLineView.EMPTY);
            return;
        }
        // split
        int lineStart = 0;
        int lineWidth = 0;
        var lexer = new WenyanRLexer(CharStreams.fromString(backend.getContent()));
        lexer.removeErrorListeners();
        var tokens = new CommonTokenStream(lexer);
        StyledStringView currentToken = updateToken(tokens, 0);
        List<StyledStringView> currentStyles = new ArrayList<>();
        for (int i = 0; i < backend.getContent().length(); i++) {
            if (i > currentToken.endIndex()) {
                currentStyles.add(new StyledStringView(
                        Math.max(currentToken.beginIndex(), lineStart),
                        currentToken.endIndex() + 1, currentToken.token()));
                currentToken = updateToken(tokens, i);
            }

            char c = backend.getContent().charAt(i);
            if (c == '\n') {
                cutToken(i, currentToken, currentStyles, lineStart);
                displayLines.add(new StyledLineView(lineStart, i, currentStyles));
                currentStyles = new ArrayList<>();
                lineStart = i + 1;
                lineWidth = 0;
                continue;
            }
            int charWidth = font.width(String.valueOf(c));
            if (lineWidth + charWidth > codeWidth) {
                cutToken(i, currentToken, currentStyles, lineStart);
                displayLines.add(new StyledLineView(lineStart, i, currentStyles));
                currentStyles = new ArrayList<>();
                lineStart = i;
                lineWidth = charWidth;
            } else {
                lineWidth += charWidth;
            }
        }

        // the last line
        if (lineStart < backend.getContent().length()) {
            // assert stopIndex >= length - 1 (as i(= length - 2) < stopIndex)
            // -> min(stopIndex + 1, length) = length
            currentStyles.add(new StyledStringView(Math.max(currentToken.beginIndex(), lineStart), backend.getContent().length(), currentToken.token()));
            displayLines.add(new StyledLineView(lineStart, backend.getContent().length(), currentStyles));
        }

        if (backend.getContent().charAt(backend.getContent().length() - 1) == '\n') {
            displayLines.add(new StyledLineView(backend.getContent().length(), backend.getContent().length(),
                    List.of(new StyledStringView(backend.getContent().length(), backend.getContent().length(), -1))));
        }
    }

    private static @NotNull StyledStringView updateToken(CommonTokenStream tokens, int i) {
        StyledStringView currentToken;
        var token = tokens.LT(1);
        if (i >= token.getStartIndex()) {
            tokens.consume();
            currentToken = new StyledStringView(token.getStartIndex(), token.getStopIndex(), token.getType());
        } else {
            currentToken = new StyledStringView(i, token.getStartIndex() - 1, -1);
        }
        return currentToken;
    }

    private static void cutToken(int breakPoint, StyledStringView token, List<StyledStringView> currentStyle, int lineStart) {
        // if break inside token -> cut
        // assert i <= stopIndex
        if (breakPoint > token.beginIndex() || currentStyle.isEmpty()) { // add a empty style to ensure at least one style
            currentStyle.add(new StyledStringView(
                    Math.max(token.beginIndex(), lineStart), breakPoint,
                    token.token()));
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
    public record StyledLineView(List<StyledStringView> styles) {
        public StyledLineView(int beginIndex, int endIndex, List<StyledStringView> styles) {
            this(styles);
            if (endIndex != endIndex() || beginIndex != beginIndex())
                throw new IllegalArgumentException(beginIndex + " " + endIndex + " " + styles);
        }

        static final StyledLineView EMPTY = new StyledLineView(0, 0, List.of(new StyledStringView(0, 0, -1)));

        public StringView stringView() {
            return new StringView(beginIndex(), endIndex());
        }

        public int beginIndex() {
            return styles().getFirst().beginIndex();
        }

        public int endIndex() {
            return styles().getLast().endIndex();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public record StyledStringView(int beginIndex, int endIndex, int token) {
    }

    @OnlyIn(Dist.CLIENT)
    public record Placeholder(generated_Snippets.Context context, int index) {
    }
}
