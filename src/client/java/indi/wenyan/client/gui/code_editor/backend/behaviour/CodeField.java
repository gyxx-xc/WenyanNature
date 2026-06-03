package indi.wenyan.client.gui.code_editor.backend.behaviour;

import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditBackend;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

// copy from net.minecraft.client.gui.components.MultilineTextField
public class CodeField {
    private static final int LINE_SEEK_PIXEL_BIAS = 2;

    private final Font font;

    @Getter
    private List<FormattedLine> displayLines;

    @Setter
    boolean selecting = false;

    private final CodeEditBackend backend;

    private final Supplier<Integer> widthUpdater;

    public CodeField(Font font, CodeEditBackend backend,
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

    public boolean hasSelection() {
        return backend.getSelectCursor() != backend.getCursor();
    }

    public IStringView getSelected() {
        return new StringView(Math.min(backend.getSelectCursor(), backend.getCursor()), Math.max(backend.getSelectCursor(), backend.getCursor()));
    }

    public int getLineAtCursor() {
        for (int i = 0; i < displayLines.size(); ++i) {
            var stringView = displayLines.get(i);
            if (backend.getCursor() >= stringView.beginIndex() && backend.getCursor() <= stringView.endIndex()) {
                return i;
            }
        }

        return -1;
    }

    public void seekCursorToPoint(double x, double y) {
        int cursorY = Mth.floor(y / font.lineHeight);
        var stringView = displayLines.get(Mth.clamp(cursorY, 0, displayLines.size() - 1));
        String line = backend.getContent().substring(stringView.beginIndex(), stringView.endIndex());
        int cursorX = font.plainSubstrByWidth(line, Mth.floor(x)).length();
        if (cursorX < line.length()) {
            double inChar = x - font.width(line.substring(0, cursorX));
            cursorX += (inChar / font.width(line.substring(cursorX, cursorX + 1))) > 0.5 ? 1 : 0;
        }
        seekCursor(Whence.ABSOLUTE, stringView.beginIndex() + cursorX);
    }

    public boolean keyPressed(KeyEvent event) {
        selecting = event.hasShiftDown();
        if (handlePreDefined(event))
            return true;
        else if (handleIgnoreModifiers(event.key()))
            return true;
        else if (event.hasControlDown()) {
            switch (event.key()) {
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
            switch (event.key()) {
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
                    if (!success) backend.insertText("    ");
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }


    private IStringView getPreviousWord() {
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

    private IStringView getNextWord() {
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

    private IStringView getCursorLineView(int offset) {
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
            IStringView cursorLineView = getCursorLineView(offset);
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
        backend.insertText("");
    }

    private boolean handleIgnoreModifiers(int keyCode) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                backend.insertText("\n");
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

    private boolean handlePreDefined(KeyEvent event) {
        if (event.isSelectAll()) {
            backend.setCursor(backend.getContent().length());
            backend.setSelectCursor(0);
            return true;
        } else if (event.isCopy()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(
                    backend.getContent()
                            .substring(getSelected().beginIndex(), getSelected().endIndex()));
            return true;
        } else if (event.isPaste()) {
            backend.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (event.isCut()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(
                    backend.getContent()
                            .substring(getSelected().beginIndex(), getSelected().endIndex()));
            backend.insertText("");
            return true;
        } else {
            return false;
        }
    }

    private void onValueChange() {
        if (backend.getContent().isEmpty()) {
            List<FormattedLine> newLines = new ArrayList<>();
            newLines.add(new FormattedLine(0));
            displayLines = newLines;
            return;
        }

        displayLines = CodeFormatter.splitLine(font,
                CodeFormatter.highlightCode(backend.getContent()),
                widthUpdater.get());
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


    public record StringView(int beginIndex, int endIndex) implements IStringView {
        public static final IStringView EMPTY = new StringView(0, 0);
    }

    public record Placeholder(generated_Snippets.Context context, int index) {
    }
}

