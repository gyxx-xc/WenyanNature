package indi.wenyan.content.gui;

import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.experimental.Delegate;
import lombok.val;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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

    private Style styleFromTokenType(int tokenType) {
        val CONTROL_STYLE = Style.EMPTY.withColor(0xFFB400);
        val STRING_STYLE = Style.EMPTY.withColor(0x008000);
        val DATA_STYLE = Style.EMPTY.withColor(0x1C00CF);
        val COMMENT_STYLE = Style.EMPTY.withColor(0xAAAAAA);
        val IDENTIFIER_STYLE = Style.EMPTY.withColor(0x005CC5);
        val OPERATOR_STYLE = Style.EMPTY.withColor(0xD73A49);
        val TYPE_STYLE = Style.EMPTY.withColor(0x795E26);
        val DEFAULT_STYLE = Style.EMPTY.withColor(0x000000);
/*
        things in default

		FUNCTION_ARGS_START=30, FUNCTION_ARGS_GET=31, FUNCTION_BODY_START=32, FUNCTION_DEFINE_END=33, FUNCTION_GET_ARGS=34,
		OBJECT_BODY_START=35, OBJECT_DEFINE_END=36, OBJECT_STATIC_DECLARE=37, EXTENDS=49,
		DEFINE_CLOSURE=40,

        ASSIGN_LEFT=43, ASSIGN_RIGHT_NULL=20, ASSIGN_RIGHT_END=21, ASSIGN_RIGHT=22,
		LOCAL_DECLARE_OP=38, GLOBAL_DECLARE_OP=39,
		NAMING=42, DECLARE_HAVE=44, YUE=52,
		PREPOSITION_LEFT=45, PREPOSITION_RIGHT=46,
		CALLING_FUNCTION=47, CREATE_OBJECT=48,
		WS=78, NEWLINE=79;
        */
        return switch (tokenType) {
            // control
            case WenyanRLexer.RETURN_NULL, WenyanRLexer.RETURN, WenyanRLexer.RETURN_LAST,
                 WenyanRLexer.BREAK_, WenyanRLexer.CONTINUE_, WenyanRLexer.IF_, WenyanRLexer.ELSE_,
                 WenyanRLexer.FOR_WHILE_SART, WenyanRLexer.FOR_ARR_BELONG, WenyanRLexer.FOR_ENUM_START,
                 WenyanRLexer.FOR_ARR_START, WenyanRLexer.FOR_ENUM_TIMES, WenyanRLexer.FOR_IF_END, WenyanRLexer.ZHE ->
                    CONTROL_STYLE;
            // string
            case WenyanRLexer.STRING_LITERAL -> STRING_STYLE;
            // data
            case WenyanRLexer.FLOAT_NUM, WenyanRLexer.INT_NUM, WenyanRLexer.BOOL_VALUE -> DATA_STYLE;
            // comment
            case WenyanRLexer.COMMENT -> COMMENT_STYLE;
            // identifier
            case WenyanRLexer.IDENTIFIER, WenyanRLexer.LONG, WenyanRLexer.SELF, WenyanRLexer.PARENT,
                 WenyanRLexer.DATA_ID_LAST, WenyanRLexer.ZHI -> IDENTIFIER_STYLE;
            // operator
            case WenyanRLexer.ADD, WenyanRLexer.SUB, WenyanRLexer.MUL,
                 WenyanRLexer.DIV, WenyanRLexer.UNARY_OP, WenyanRLexer.ARRAY_COMBINE_OP,
                 WenyanRLexer.ARRAY_ADD_OP, WenyanRLexer.WRITE_KEY_FUNCTION, WenyanRLexer.POST_MOD_MATH_OP,
                 WenyanRLexer.AND, WenyanRLexer.OR, WenyanRLexer.NEQ, WenyanRLexer.LTE,
                 WenyanRLexer.GTE, WenyanRLexer.EQ, WenyanRLexer.GT, WenyanRLexer.LT -> OPERATOR_STYLE;
            // type
            case WenyanRLexer.BOOL_TYPE, WenyanRLexer.STRING_TYPE, WenyanRLexer.LIST_TYPE, WenyanRLexer.OBJECT_TYPE,
                 WenyanRLexer.FUNCTION_TYPE, WenyanRLexer.NUM_TYPE -> TYPE_STYLE;
            default -> DEFAULT_STYLE;
        };
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
            int currentY = getY() + innerPadding();
            int styleCounter = 0;

            for (var stringView : textField.getDisplayLines()) {
                if (stringView.beginIndex() != stringView.endIndex() &&
                        withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                    if (isCursorRender && cursorInContent &&
                            cursor >= stringView.beginIndex() && cursor <= stringView.endIndex()) {
                        // cursor
                        cursorX = getX() + innerPadding() +
                                font.width(content.substring(stringView.beginIndex(), cursor)) - 1;
                        guiGraphics.fill(cursorX, currentY - 1,
                                cursorX + 1, currentY + 1 + font.lineHeight,
                                CURSOR_INSERT_COLOR);
                    }
                    int lastEnd = stringView.beginIndex();
                    cursorX = getX() + innerPadding();
                    do {
                        int end;
                        do {
                            end = textField.getStyleMarks().get(styleCounter).endIndex();
                        } while (end <= stringView.beginIndex() && ++styleCounter < textField.getStyleMarks().size());
                        String line = content.substring(lastEnd,
                                Math.clamp(end, stringView.beginIndex(), stringView.endIndex()));
                        var style = styleFromTokenType(textField.getStyleMarks().get(styleCounter).style());
                        cursorX = guiGraphics.drawString(font, Component.literal(line).withStyle(style),
                                cursorX, currentY,
                                0xFFFFFFFF, false);
                        lastEnd = end;
                        if (end < stringView.endIndex()) {
                            styleCounter++;
                        }
                    } while (lastEnd < stringView.endIndex());
                }
                currentY += font.lineHeight;
            }

            int cursorY = currentY - font.lineHeight;
            if (isCursorRender && !cursorInContent &&
                    withinContentAreaTopBottom(cursorY, cursorY + font.lineHeight)) {
                guiGraphics.drawString(font, CURSOR_APPEND_CHARACTER, getX() + innerPadding(), cursorY, CURSOR_INSERT_COLOR);
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
        return 3 * font.lineHeight;
    }
}
