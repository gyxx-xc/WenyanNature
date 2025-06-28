package indi.wenyan.content.gui;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
@OnlyIn(Dist.CLIENT)
public class TextFieldWidget extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_COLOR = 0xffd0d0d0;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int TEXT_COLOR = 0xffe0e0e0;
    private static final int PLACEHOLDER_TEXT_COLOR = 0xcce0e0e0;
    private final Font font;
    private final Component placeholder;
    private final TextField textField;
    private long focusedTime = Util.getMillis();

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {

    }

    public TextFieldWidget(Font font, int x, int y, int width, int height, Component placeholder, Component message) {
        super(x, y, width, height, message);
        this.font = font;
        this.placeholder = placeholder;
        textField = new TextField(font, width - totalInnerPadding());
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
        TextField.StringView multilinetextfield$stringview = this.textField.getLineView((int)(d0 / (double)9.0F));
        if (this.textField.getCursor() <= multilinetextfield$stringview.beginIndex()) {
            d0 = this.textField.getLineAtCursor() * 9;
        } else {
            TextField.StringView multilinetextfield$stringview1 = this.textField.getLineView((int)((d0 + (double)this.height) / (double)9.0F) - 1);
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

}
