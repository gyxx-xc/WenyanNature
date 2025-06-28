package indi.wenyan.content.gui;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;
import java.util.function.Consumer;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
@OnlyIn(Dist.CLIENT)
public class TextField extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_COLOR = 0xffd0d0d0;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int TEXT_COLOR = 0xffe0e0e0;
    private static final int PLACEHOLDER_TEXT_COLOR = 0xcce0e0e0;
    private final Font font;
    private final Component placeholder;
    private final MultilineTextField textField;
    private long focusedTime = Util.getMillis();

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {

    }

    public TextField(Font font, int x, int y, int width, int height, Component placeholder, Component message) {
        super(x, y, width, height, message);
        this.font = font;
        this.placeholder = placeholder;
        textField = new MultilineTextField(font, width - totalInnerPadding());
        textField.setCursorListener(this::scrollToCursor);
    }

    public void setCharacterLimit(int characterLimit) {
        this.textField.setCharacterLimit(characterLimit);
    }

    public void setValueListener(Consumer<String> valueListener) {
        this.textField.setValueListener(valueListener);
    }

    public void setValue(String fullText) {
        this.textField.setValue(fullText);
    }

    public String getValue() {
        return this.textField.getValue();
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            this.textField.setSelecting(Screen.hasShiftDown());
            this.seekCursorScreen(mouseX, mouseY);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        } else if (this.withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            this.textField.setSelecting(true);
            this.seekCursorScreen(mouseX, mouseY);
            this.textField.setSelecting(Screen.hasShiftDown());
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (this.visible && this.isFocused() && StringUtil.isAllowedChatCharacter(codePoint)) {
            this.textField.insertText(Character.toString(codePoint));
            return true;
        } else {
            return false;
        }
    }

    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String content = this.textField.getValue();
        if (content.isEmpty() && !this.isFocused()) {
            guiGraphics.drawWordWrap(this.font, this.placeholder, this.getX() + this.innerPadding(), this.getY() + this.innerPadding(), this.width - this.totalInnerPadding(), PLACEHOLDER_TEXT_COLOR);
        } else {
            int cursor = this.textField.getCursor();
            boolean isCursorRender = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L;
            boolean cursorInContent = cursor < content.length();
            int cursorX = 0;
            int cursorY = 0;
            int currentY = this.getY() + this.innerPadding();

            for(var stringView : this.textField.iterateLines()) {
                boolean withinContent = this.withinContentAreaTopBottom(currentY, currentY + 9);
                if (isCursorRender && cursorInContent && cursor >= stringView.beginIndex() && cursor <= stringView.endIndex()) {
                    if (withinContent) {
                        cursorX = guiGraphics.drawString(this.font,
                                content.substring(stringView.beginIndex(), cursor),
                                this.getX() + this.innerPadding(), currentY, TEXT_COLOR) - 1;
                        // content
                        guiGraphics.drawString(this.font, content.substring(cursor, stringView.endIndex()),
                                cursorX, currentY, TEXT_COLOR);
                        // cursor
                        guiGraphics.fill(cursorX, currentY - 1, cursorX + 1, currentY + 1 + 9, CURSOR_INSERT_COLOR);
                    }
                } else {
                    if (withinContent) {
                        cursorX = guiGraphics.drawString(this.font,
                                content.substring(stringView.beginIndex(), stringView.endIndex()),
                                this.getX() + this.innerPadding(), currentY,
                                TEXT_COLOR) - 1;
                    }
                    cursorY = currentY;
                }
                currentY += 9;
            }

            if (isCursorRender && !cursorInContent && this.withinContentAreaTopBottom(cursorY, cursorY + 9)) {
                guiGraphics.drawString(this.font, CURSOR_APPEND_CHARACTER, cursorX, cursorY, CURSOR_INSERT_COLOR);
            }

            if (this.textField.hasSelection()) {
                var selected = this.textField.getSelected();
                int k1 = this.getX() + this.innerPadding();
                currentY = this.getY() + this.innerPadding();

                for(var stringView : this.textField.iterateLines()) {
                    if (selected.beginIndex() <= stringView.endIndex()) {
                        if (stringView.beginIndex() > selected.endIndex()) {
                            break;
                        }

                        if (this.withinContentAreaTopBottom(currentY, currentY + 9)) {
                            int i1 = this.font.width(content.substring(stringView.beginIndex(), Math.max(selected.beginIndex(), stringView.beginIndex())));
                            int j1;
                            if (selected.endIndex() > stringView.endIndex()) {
                                j1 = this.width - this.innerPadding();
                            } else {
                                j1 = this.font.width(content.substring(stringView.beginIndex(), selected.endIndex()));
                            }

                            this.renderHighlight(guiGraphics, k1 + i1, currentY, k1 + j1, currentY + 9);
                        }
                    }
                    currentY += 9;
                }
            }
        }
    }

    protected void renderDecorations(@NotNull GuiGraphics guiGraphics) {
        super.renderDecorations(guiGraphics);
        if (this.textField.hasCharacterLimit()) {
            int i = this.textField.getCharacterLimit();
            Component component = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.getValue().length(), i);
            guiGraphics.drawString(this.font, component, this.getX() + this.width - this.font.width(component), this.getY() + this.height + 4, 10526880);
        }

    }

    public int getInnerHeight() {
        return 9 * this.textField.getLineCount();
    }

    protected boolean scrollbarVisible() {
        return (double)this.textField.getLineCount() > this.getDisplayableLineCount();
    }

    protected double scrollRate() {
        return 4.5F;
    }

    private void renderHighlight(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        guiGraphics.fill(RenderType.guiTextHighlight(), minX, minY, maxX, maxY, -16776961);
    }

    private void scrollToCursor() {
        double d0 = this.scrollAmount();
        MultilineTextField.StringView multilinetextfield$stringview = this.textField.getLineView((int)(d0 / (double)9.0F));
        if (this.textField.getCursor() <= multilinetextfield$stringview.beginIndex()) {
            d0 = this.textField.getLineAtCursor() * 9;
        } else {
            MultilineTextField.StringView multilinetextfield$stringview1 = this.textField.getLineView((int)((d0 + (double)this.height) / (double)9.0F) - 1);
            if (this.textField.getCursor() > multilinetextfield$stringview1.endIndex()) {
                d0 = this.textField.getLineAtCursor() * 9 - this.height + 9 + this.totalInnerPadding();
            }
        }

        this.setScrollAmount(d0);
    }

    private double getDisplayableLineCount() {
        return (double)(this.height - this.totalInnerPadding()) / (double)9.0F;
    }

    private void seekCursorScreen(double mouseX, double mouseY) {
        double d0 = mouseX - (double)this.getX() - (double)this.innerPadding();
        double d1 = mouseY - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
        this.textField.seekCursorToPoint(d0, d1);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.focusedTime = Util.getMillis();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static class MultilineTextField {
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

        public MultilineTextField(Font font, int width) {
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
            this.value = this.truncateFullText(fullText);
            this.cursor = this.value.length();
            this.selectCursor = this.cursor;
            this.onValueChange();
        }

        public void insertText(String text) {
            if (!text.isEmpty() || this.hasSelection()) {
                String s = this.truncateInsertionText(StringUtil.filterText(text, true));
                StringView multilinetextfield$stringview = this.getSelected();
                this.value = (new StringBuilder(this.value)).replace(multilinetextfield$stringview.beginIndex, multilinetextfield$stringview.endIndex, s).toString();
                this.cursor = multilinetextfield$stringview.beginIndex + s.length();
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
            for(int i = 0; i < this.displayLines.size(); ++i) {
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
            int j = Mth.floor(y / (double)9.0F);
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

        @VisibleForTesting
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

        @VisibleForTesting
        public StringView getPreviousWord() {
            if (this.value.isEmpty()) {
                return MultilineTextField.StringView.EMPTY;
            } else {
                int i;
                for(i = Mth.clamp(this.cursor, 0, this.value.length() - 1); i > 0 && Character.isWhitespace(this.value.charAt(i - 1)); --i) {
                }

                while(i > 0 && !Character.isWhitespace(this.value.charAt(i - 1))) {
                    --i;
                }

                return new StringView(i, this.getWordEndPosition(i));
            }
        }

        @VisibleForTesting
        public StringView getNextWord() {
            if (this.value.isEmpty()) {
                return MultilineTextField.StringView.EMPTY;
            } else {
                int i;
                for(i = Mth.clamp(this.cursor, 0, this.value.length() - 1); i < this.value.length() && !Character.isWhitespace(this.value.charAt(i)); ++i) {
                }

                while(i < this.value.length() && Character.isWhitespace(this.value.charAt(i))) {
                    ++i;
                }

                return new StringView(i, this.getWordEndPosition(i));
            }
        }

        private int getWordEndPosition(int cursor) {
            int i;
            for(i = cursor; i < this.value.length() && !Character.isWhitespace(this.value.charAt(i)); ++i) {
            }

            return i;
        }

        private void onValueChange() {
            this.reflowDisplayLines();
            this.valueListener.accept(this.value);
            this.cursorListener.run();
        }

        private void reflowDisplayLines() {
            this.displayLines.clear();
            if (this.value.isEmpty()) {
                this.displayLines.add(MultilineTextField.StringView.EMPTY);
            } else {
                this.font.getSplitter().splitLines(this.value, this.width, Style.EMPTY, false, (p_239846_, p_239847_, p_239848_) -> this.displayLines.add(new StringView(p_239847_, p_239848_)));
                if (this.value.charAt(this.value.length() - 1) == '\n') {
                    this.displayLines.add(new StringView(this.value.length(), this.value.length()));
                }
            }

        }

        private String truncateFullText(String fullText) {
            return this.hasCharacterLimit() ? StringUtil.truncateStringIfNecessary(fullText, this.characterLimit, false) : fullText;
        }

        private String truncateInsertionText(String text) {
            if (this.hasCharacterLimit()) {
                int i = this.characterLimit - this.value.length();
                return StringUtil.truncateStringIfNecessary(text, i, false);
            } else {
                return text;
            }
        }

        @OnlyIn(Dist.CLIENT)
        public record StringView(int beginIndex, int endIndex) {
            static final StringView EMPTY = new StringView(0, 0);
        }
    }

}
