package indi.wenyan.content.gui;

import lombok.experimental.Delegate;
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
    private static final int TEXT_COLOR = 0xff0e0e0e;

    private final Font font;
    private long focusedTime = Util.getMillis(); // for blink

    @Delegate(types = FromTextField.class)
    private final TextField textField;
    @SuppressWarnings("unused") // ide don't know this lombok delegate
    private interface FromTextField {
        void setCharacterLimit(int characterLimit);
        void setValueListener(Consumer<String> valueListener);
        void setValue(String fullText);
        String getValue();
    }

    public TextFieldWidget(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        textField = new TextField(font, width - totalInnerPadding());
        textField.setCursorListener(() -> {
            double scrollAmount = scrollAmount();
            var displayLines = textField.getDisplayLines();

            int beginIndex = displayLines.get((int) (scrollAmount / font.lineHeight)).beginIndex();
            if (textField.getCursor() <= beginIndex) {
                scrollAmount = textField.getLineAtCursor() * font.lineHeight;
            } else if ((int) ((scrollAmount + height) / font.lineHeight) - 1 < displayLines.size()) {
                int endIndex = displayLines.get((int) ((scrollAmount + height) / font.lineHeight) - 1).endIndex();
                if (textField.getCursor() > endIndex) {
                    scrollAmount = textField.getLineAtCursor() * font.lineHeight - height + font.lineHeight + totalInnerPadding();
                }
            }

            setScrollAmount(scrollAmount);
        });
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", getMessage(), getValue()));
    }

    // input
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            textField.setSelecting(Screen.hasShiftDown());
            textField.seekCursorToPoint(mouseX - getX() - innerPadding(),
                    mouseY - getY() - innerPadding() + scrollAmount());
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
            textField.seekCursorToPoint(mouseX - getX() - innerPadding(),
                    mouseY - getY() - innerPadding() + scrollAmount());
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

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            focusedTime = Util.getMillis();
        }
    }

    // rendering
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String content = textField.getValue();
        if (!content.isEmpty() || isFocused()) {
            int cursor = textField.getCursor();
            boolean isCursorRender = isFocused() && (Util.getMillis() - focusedTime) / 300L % 2L == 0L;
            boolean cursorInContent = cursor < content.length();
            int cursorX = 0;
            int cursorY = 0;
            int currentY = getY() + innerPadding();

            for (var stringView : textField.getDisplayLines()) {
                boolean withinContent = withinContentAreaTopBottom(currentY, currentY + font.lineHeight);
                if (isCursorRender && cursorInContent && cursor >= stringView.beginIndex() && cursor <= stringView.endIndex()) {
                    if (withinContent) {
                        // content
                        cursorX = guiGraphics.drawString(font,
                                content.substring(stringView.beginIndex(), cursor),
                                getX() + innerPadding(), currentY, TEXT_COLOR, false) - 1;
                        guiGraphics.drawString(font, content.substring(cursor, stringView.endIndex()),
                                cursorX+1, currentY, TEXT_COLOR, false);
                        // cursor
                        guiGraphics.fill(cursorX, currentY - 1, cursorX + 1, currentY + 1 + font.lineHeight, CURSOR_INSERT_COLOR);
                    }
                } else {
                    if (withinContent) {
                        cursorX = guiGraphics.drawString(font,
                                content.substring(stringView.beginIndex(), stringView.endIndex()),
                                getX() + innerPadding(), currentY,
                                TEXT_COLOR, false) - 1;
                    }
                    cursorY = currentY;
                }
                currentY += font.lineHeight;
            }

            if (isCursorRender && !cursorInContent && withinContentAreaTopBottom(cursorY, cursorY + font.lineHeight)) {
                guiGraphics.drawString(font, CURSOR_APPEND_CHARACTER, cursorX, cursorY, CURSOR_INSERT_COLOR);
            }

            if (textField.hasSelection()) {
                var selected = textField.getSelected();
                int k1 = getX() + innerPadding();
                currentY = getY() + innerPadding();

                for (var stringView : textField.getDisplayLines()) {
                    if (selected.beginIndex() <= stringView.endIndex()) {
                        if (stringView.beginIndex() > selected.endIndex()) {
                            break;
                        }

                        if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                            int i1 = font.width(content.substring(stringView.beginIndex(), Math.max(selected.beginIndex(), stringView.beginIndex())));
                            int j1;
                            if (selected.endIndex() > stringView.endIndex()) {
                                j1 = width - innerPadding();
                            } else {
                                j1 = font.width(content.substring(stringView.beginIndex(), selected.endIndex()));
                            }
                            guiGraphics.fill(RenderType.guiTextHighlight(),
                                    k1 + i1, currentY, k1 + j1, currentY + font.lineHeight,
                                    0xff0000ff);
                        }
                    }
                    currentY += font.lineHeight;
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

    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {

    }

    // scrolling
    public int getInnerHeight() {
        return font.lineHeight * textField.getDisplayLines().size();
    }

    protected boolean scrollbarVisible() {
        return textField.getDisplayLines().size() > (height - totalInnerPadding()) / (double) font.lineHeight;
    }

    protected double scrollRate() {
        return 3*font.lineHeight;
    }
}
