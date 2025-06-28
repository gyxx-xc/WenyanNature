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
    private static final int LINE_HEIGHT = 9;
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
        textField.setCharacterLimit(characterLimit);
    }

    public void setValueListener(Consumer<String> valueListener) {
        textField.setValueListener(valueListener);
    }

    public void setValue(String fullText) {
        textField.setValue(fullText);
    }

    public String getValue() {
        return textField.getValue();
    }

    private void scrollToCursor() {
        double scrollAmount = scrollAmount();
        TextField.StringView stringView = textField.getLineView((int) (scrollAmount / LINE_HEIGHT));
        if (textField.getCursor() <= stringView.beginIndex()) {
            scrollAmount = textField.getLineAtCursor() * LINE_HEIGHT;
        } else {
            TextField.StringView multilinetextfield$stringView1 = textField.getLineView((int) ((scrollAmount + height) / LINE_HEIGHT) - 1);
            if (textField.getCursor() > multilinetextfield$stringView1.endIndex()) {
                scrollAmount = textField.getLineAtCursor() * LINE_HEIGHT - height + LINE_HEIGHT + totalInnerPadding();
            }
        }

        setScrollAmount(scrollAmount);
    }

    private void seekCursorScreen(double mouseX, double mouseY) {
        textField.seekCursorToPoint(mouseX - getX() - innerPadding(),
                mouseY - getY() - innerPadding() + scrollAmount());
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", getMessage(), getValue()));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            textField.setSelecting(Screen.hasShiftDown());
            seekCursorScreen(mouseX, mouseY);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        } else if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            textField.setSelecting(true);
            seekCursorScreen(mouseX, mouseY);
            textField.setSelecting(Screen.hasShiftDown());
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (visible && isFocused() && StringUtil.isAllowedChatCharacter(codePoint)) {
            textField.insertText(Character.toString(codePoint));
            return true;
        } else {
            return false;
        }
    }

    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String content = textField.getValue();
        if (content.isEmpty() && !isFocused()) {
            guiGraphics.drawWordWrap(font, placeholder, getX() + innerPadding(), getY() + innerPadding(), width - totalInnerPadding(), PLACEHOLDER_TEXT_COLOR);
        } else {
            int cursor = textField.getCursor();
            boolean isCursorRender = isFocused() && (Util.getMillis() - focusedTime) / 300L % 2L == 0L;
            boolean cursorInContent = cursor < content.length();
            int cursorX = 0;
            int cursorY = 0;
            int currentY = getY() + innerPadding();

            for (var stringView : textField.iterateLines()) {
                boolean withinContent = withinContentAreaTopBottom(currentY, currentY + LINE_HEIGHT);
                if (isCursorRender && cursorInContent && cursor >= stringView.beginIndex() && cursor <= stringView.endIndex()) {
                    if (withinContent) {
                        cursorX = guiGraphics.drawString(font,
                                content.substring(stringView.beginIndex(), cursor),
                                getX() + innerPadding(), currentY, TEXT_COLOR) - 1;
                        // content
                        guiGraphics.drawString(font, content.substring(cursor, stringView.endIndex()),
                                cursorX, currentY, TEXT_COLOR);
                        // cursor
                        guiGraphics.fill(cursorX, currentY - 1, cursorX + 1, currentY + 1 + LINE_HEIGHT, CURSOR_INSERT_COLOR);
                    }
                } else {
                    if (withinContent) {
                        cursorX = guiGraphics.drawString(font,
                                content.substring(stringView.beginIndex(), stringView.endIndex()),
                                getX() + innerPadding(), currentY,
                                TEXT_COLOR) - 1;
                    }
                    cursorY = currentY;
                }
                currentY += LINE_HEIGHT;
            }

            if (isCursorRender && !cursorInContent && withinContentAreaTopBottom(cursorY, cursorY + LINE_HEIGHT)) {
                guiGraphics.drawString(font, CURSOR_APPEND_CHARACTER, cursorX, cursorY, CURSOR_INSERT_COLOR);
            }

            if (textField.hasSelection()) {
                var selected = textField.getSelected();
                int k1 = getX() + innerPadding();
                currentY = getY() + innerPadding();

                for (var stringView : textField.iterateLines()) {
                    if (selected.beginIndex() <= stringView.endIndex()) {
                        if (stringView.beginIndex() > selected.endIndex()) {
                            break;
                        }

                        if (withinContentAreaTopBottom(currentY, currentY + LINE_HEIGHT)) {
                            int i1 = font.width(content.substring(stringView.beginIndex(), Math.max(selected.beginIndex(), stringView.beginIndex())));
                            int j1;
                            if (selected.endIndex() > stringView.endIndex()) {
                                j1 = width - innerPadding();
                            } else {
                                j1 = font.width(content.substring(stringView.beginIndex(), selected.endIndex()));
                            }
                            guiGraphics.fill(RenderType.guiTextHighlight(),
                                    k1 + i1, currentY, k1 + j1, currentY + LINE_HEIGHT,
                                    0xff0000ff);
                        }
                    }
                    currentY += LINE_HEIGHT;
                }
            }
        }
    }

    protected void renderDecorations(@NotNull GuiGraphics guiGraphics) {
        super.renderDecorations(guiGraphics);
        if (textField.hasCharacterLimit()) {
            int i = textField.getCharacterLimit();
            Component component = Component.translatable("gui.multiLineEditBox.character_limit", textField.getValue().length(), i);
            guiGraphics.drawString(this.font, component, this.getX() + this.width - this.font.width(component), this.getY() + this.height + 4, 0xa0a0a0);
        }

    }

    public int getInnerHeight() {
        return LINE_HEIGHT * textField.getLineCount();
    }

    protected boolean scrollbarVisible() {
        return textField.getLineCount() > (height - totalInnerPadding()) / (double) LINE_HEIGHT;
    }

    protected double scrollRate() {
        return 4.5F;
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            focusedTime = Util.getMillis();
        }
    }
}
