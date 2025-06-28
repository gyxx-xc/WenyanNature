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

import java.util.List;
import java.util.function.Consumer;

// from net.minecraft.client.gui.components.MultilineTextField;
@OnlyIn(Dist.CLIENT)
public class TextField {
    public static final int NO_CHARACTER_LIMIT = Integer.MAX_VALUE;
    private static final int LINE_SEEK_PIXEL_BIAS = 2;
    private final Font font;
    private final List<StringView> displayLines = Lists.newArrayList();
    @Getter private String value;
    @Getter private int cursor;
    private int selectCursor;
    @Setter private boolean selecting;
    @Getter private int characterLimit = NO_CHARACTER_LIMIT;
    private final int width;
    @Setter private Consumer<String> valueListener = (p_239235_) -> {};
    @Setter private Runnable cursorListener = () -> {};

    public TextField(Font font, int width) {
        this.font = font;
        this.width = width;
        this.setValue("");
    }

    public void setCharacterLimit(int characterLimit) {
        if (characterLimit < 0) {
            throw new IllegalArgumentException("Character limit cannot be negative");
        } else {
            this.characterLimit = characterLimit;
        }
    }

    public boolean hasCharacterLimit() {
        return this.characterLimit != NO_CHARACTER_LIMIT;
    }

    public void setValue(String fullText) {
        this.value = this.hasCharacterLimit() ?
                StringUtil.truncateStringIfNecessary(fullText, this.characterLimit, false) : fullText;
        this.cursor = this.value.length();
        this.selectCursor = this.cursor;
        this.onValueChange();
    }

    public void insertText(String text) {
        if (!text.isEmpty() || this.hasSelection()) {
            String filteredText = StringUtil.filterText(text, true);
            String string = this.hasCharacterLimit() ?
                    StringUtil.truncateStringIfNecessary(filteredText, this.characterLimit - this.value.length(), false)
                    : filteredText;
            StringView stringView = this.getSelected();
            this.value = (new StringBuilder(this.value)).replace(stringView.beginIndex, stringView.endIndex, string).toString();
            this.cursor = stringView.beginIndex + string.length();
            this.selectCursor = this.cursor;
            this.onValueChange();
        }
    }

    public void deleteText(int length) {
        if (!this.hasSelection())
            this.selectCursor = Mth.clamp(this.cursor + length, 0, this.value.length());
        this.insertText("");
    }

    public StringView getSelected() {
        return new StringView(Math.min(this.selectCursor, this.cursor), Math.max(this.selectCursor, this.cursor));
    }

    public int getLineCount() {
        return this.displayLines.size();
    }

    public int getLineAtCursor() {
        for (int i = 0; i < this.displayLines.size(); ++i) {
            StringView multilinetextfield$stringview = this.displayLines.get(i);
            if (this.cursor >= multilinetextfield$stringview.beginIndex && this.cursor <= multilinetextfield$stringview.endIndex) {
                return i;
            }
        }

        return -1;
    }

    public StringView getLineView(int lineNumber) {
        return this.displayLines.get(Mth.clamp(lineNumber, 0, this.displayLines.size() - 1));
    }

    public void seekCursor(Whence whence, int position) {
        switch (whence) {
            case ABSOLUTE -> this.cursor = position;
            case RELATIVE -> this.cursor += position;
            case END -> this.cursor = this.value.length() + position;
        }

        this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
        this.cursorListener.run();
        if (!this.selecting) {
            this.selectCursor = this.cursor;
        }

    }

    public void seekCursorLine(int offset) {
        if (offset != 0) {
            int i = this.font.width(this.value.substring(this.getCursorLineView().beginIndex, this.cursor)) + LINE_SEEK_PIXEL_BIAS;
            StringView multilinetextfield$stringview = this.getCursorLineView(offset);
            int j = this.font.plainSubstrByWidth(this.value.substring(multilinetextfield$stringview.beginIndex, multilinetextfield$stringview.endIndex), i).length();
            this.seekCursor(Whence.ABSOLUTE, multilinetextfield$stringview.beginIndex + j);
        }

    }

    public void seekCursorToPoint(double x, double y) {
        int i = Mth.floor(x);
        int j = Mth.floor(y / (double) 9.0F);
        StringView multilinetextfield$stringview = this.displayLines.get(Mth.clamp(j, 0, this.displayLines.size() - 1));
        int k = this.font.plainSubstrByWidth(this.value.substring(multilinetextfield$stringview.beginIndex, multilinetextfield$stringview.endIndex), i).length();
        this.seekCursor(Whence.ABSOLUTE, multilinetextfield$stringview.beginIndex + k);
    }

    public boolean keyPressed(int keyCode) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.cursor = this.value.length();
            this.selectCursor = 0;
            return true;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            this.insertText("");
            return true;
        } else {
            return switch (keyCode) {
                case 257, 335 -> {
                    this.insertText("\n");
                    yield true;
                }
                case 259 -> {
                    if (Screen.hasControlDown()) {
                        StringView multilinetextfield$stringview3 = this.getPreviousWord();
                        this.deleteText(multilinetextfield$stringview3.beginIndex - this.cursor);
                    } else {
                        this.deleteText(-1);
                    }

                    yield true;
                }
                case 261 -> {
                    if (Screen.hasControlDown()) {
                        StringView multilinetextfield$stringview2 = this.getNextWord();
                        this.deleteText(multilinetextfield$stringview2.beginIndex - this.cursor);
                    } else {
                        this.deleteText(1);
                    }

                    yield true;
                }
                case 262 -> {
                    if (Screen.hasControlDown()) {
                        StringView multilinetextfield$stringview1 = this.getNextWord();
                        this.seekCursor(Whence.ABSOLUTE, multilinetextfield$stringview1.beginIndex);
                    } else {
                        this.seekCursor(Whence.RELATIVE, 1);
                    }

                    yield true;
                }
                case 263 -> {
                    if (Screen.hasControlDown()) {
                        StringView multilinetextfield$stringview = this.getPreviousWord();
                        this.seekCursor(Whence.ABSOLUTE, multilinetextfield$stringview.beginIndex);
                    } else {
                        this.seekCursor(Whence.RELATIVE, -1);
                    }

                    yield true;
                }
                case 264 -> {
                    if (!Screen.hasControlDown()) {
                        this.seekCursorLine(1);
                    }

                    yield true;
                }
                case 265 -> {
                    if (!Screen.hasControlDown()) {
                        this.seekCursorLine(-1);
                    }

                    yield true;
                }
                case 266 -> {
                    this.seekCursor(Whence.ABSOLUTE, 0);
                    yield true;
                }
                case 267 -> {
                    this.seekCursor(Whence.END, 0);
                    yield true;
                }
                case 268 -> {
                    if (Screen.hasControlDown()) {
                        this.seekCursor(Whence.ABSOLUTE, 0);
                    } else {
                        this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().beginIndex);
                    }

                    yield true;
                }
                case 269 -> {
                    if (Screen.hasControlDown()) {
                        this.seekCursor(Whence.END, 0);
                    } else {
                        this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().endIndex);
                    }

                    yield true;
                }
                default -> false;
            };
        }
    }

    public Iterable<StringView> iterateLines() {
        return this.displayLines;
    }

    public boolean hasSelection() {
        return this.selectCursor != this.cursor;
    }

    public String getSelectedText() {
        StringView multilinetextfield$stringview = this.getSelected();
        return this.value.substring(multilinetextfield$stringview.beginIndex, multilinetextfield$stringview.endIndex);
    }

    private StringView getCursorLineView() {
        return this.getCursorLineView(0);
    }

    private StringView getCursorLineView(int offset) {
        int i = this.getLineAtCursor();
        if (i < 0) {
            int var10002 = this.cursor;
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + this.value.length() + ")");
        } else {
            return this.displayLines.get(Mth.clamp(i + offset, 0, this.displayLines.size() - 1));
        }
    }

    public StringView getPreviousWord() {
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = Mth.clamp(this.cursor, 0, this.value.length() - 1);
            while (wordStart > 0 && Character.isWhitespace(this.value.charAt(wordStart - 1))) {
                wordStart --;
            }
            while (wordStart > 0 && !Character.isWhitespace(this.value.charAt(wordStart - 1))) {
                wordStart --;
            }
            return new StringView(wordStart, this.getWordEndPosition(wordStart));
        }
    }

    public StringView getNextWord() {
        if (this.value.isEmpty()) {
            return StringView.EMPTY;
        } else {
            int wordStart = getWordEndPosition(
                    Mth.clamp(this.cursor, 0, this.value.length() - 1));
            while (wordStart < this.value.length() &&
                    Character.isWhitespace(this.value.charAt(wordStart))) {
                wordStart++;
            }
            return new StringView(wordStart, this.getWordEndPosition(wordStart));
        }
    }

    private int getWordEndPosition(int cursor) {
        while (cursor < this.value.length() &&
                !Character.isWhitespace(this.value.charAt(cursor))) {
            cursor++;
        }
        return cursor;
    }

    private void onValueChange() {
        // reflowDisplayLines
        this.displayLines.clear();
        if (this.value.isEmpty()) {
            this.displayLines.add(StringView.EMPTY);
        } else {
            this.font.getSplitter().splitLines(this.value, this.width, Style.EMPTY, false, (p_239846_, p_239847_, p_239848_) -> this.displayLines.add(new StringView(p_239847_, p_239848_)));
            if (this.value.charAt(this.value.length() - 1) == '\n') {
                this.displayLines.add(new StringView(this.value.length(), this.value.length()));
            }
        }

        this.valueListener.accept(this.value);
        this.cursorListener.run();
    }

    @OnlyIn(Dist.CLIENT)
    public record StringView(int beginIndex, int endIndex) {
        static final StringView EMPTY = new StringView(0, 0);
    }
}
