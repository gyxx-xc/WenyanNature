package indi.wenyan.content.gui;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.Getter;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
@OnlyIn(Dist.CLIENT)
public class CodeEditorWidget extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_COLOR = 0xff000000;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
            "textures/gui/edit.png");
    // todo: make it larger (sprite)
    public static final int WIDTH = 256;
    public static final int HEIGH = 192;

    // NOTE: a minecraft inner padding of 4 is also need to be considered
    private static final int scrollBarWidth = 8;
    private static final Utils.BoxInformation outerPadding =
            new Utils.BoxInformation(4, 4, 4, 4+scrollBarWidth);

    private final Font font;
    private long blinkStart = Util.getMillis(); // for blink

    private final CodeField.SavedVariable screen;

    @Getter
    private final CodeField textField;

    @Getter
    private List<SnippetSet> curSnippets = Snippets.STMT_SNIPPETS;

    public CodeEditorWidget(Font font, CodeField.SavedVariable screen, int x, int y, int width, int height) {
        super(x+outerPadding.left(), y+outerPadding.top(),
                width-outerPadding.horizontal(), height-outerPadding.vertical(),
                Component.empty());
        this.font = font;
        this.screen = screen;
        textField = new CodeField(font, screen, this.width - totalInnerPadding());
        textField.setCursorListener(()->{
            updateCurrentSnippetContext();
            scrollToCursor();
            // reset blink
            blinkStart = Util.getMillis();
        });
        // notify change
        textField.setWidthUpdater(this::lineNoWidth);
    }

    private int lineNoWidth() {
        // because the width of number is not same, return width of 0, 00, 000, ...
        // logic here: count of lines, get digit of this number, times width of "0"
        return font.width("0") * ((int) Math.log10(
                screen.getContent().chars().filter(c -> c == '\n').count() + 1) + 1)
                + innerPadding();
    }

    private void scrollToCursor() {
        double scrollAmount = scrollAmount();
        var displayLines = textField.getDisplayLines();

        int lineNo = Mth.clamp((int) (scrollAmount / font.lineHeight), 0,
                displayLines.size()-1);
        int beginIndex = displayLines.get(lineNo).beginIndex();
        if (screen.getCursor() <= beginIndex) {
            scrollAmount = textField.getLineAtCursor() * font.lineHeight;
        } else if ((int) ((scrollAmount + height) / font.lineHeight) - 1 < displayLines.size()) {
            int endIndex = displayLines.get((int) ((scrollAmount + height) / font.lineHeight) - 1).endIndex();
            if (screen.getCursor() > endIndex) {
                scrollAmount = textField.getLineAtCursor() * font.lineHeight - height + font.lineHeight + totalInnerPadding();
            }
        }

        setScrollAmount(scrollAmount);
    }

    private void updateCurrentSnippetContext() {
        int cursor = screen.getCursor();
        for (var placeholder : screen.getPlaceholders()) {
            if (cursor == placeholder.index()) {
                screen.getPlaceholders().remove(placeholder);
                curSnippets = SnippetSet.getSnippets(placeholder.context());
                return;
            }
        }
        curSnippets = Snippets.DEFAULT_SNIPPET;
    }

    private static Style styleFromTokenType(int tokenType) {
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
        narrationElementOutput.add(NarratedElementType.TITLE,
                Component.translatable("gui.narrate.editBox", getMessage(), screen.getContent()));
    }

    // input
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            screen.setSelecting(Screen.hasShiftDown());
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
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
            screen.setSelecting(true);
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
                    mouseY - getY() - innerPadding() + scrollAmount());
            screen.setSelecting(Screen.hasShiftDown());
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
            blinkStart = Util.getMillis();
        }
    }

    // rendering
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        StringBuilder content = screen.getContent();
        int cursor = screen.getCursor();
        boolean isCursorRender = !isFocused() ||
                (Util.getMillis() - blinkStart - 100L) / 500L % 2L == 0L;
        boolean cursorInContent = cursor < content.length();
        int cursorX = 0;
        int currentY = getY() + innerPadding();
        int lineNo = 1;
        boolean isContinuedLine = false;
        int styleCounter = 0;
        int placeholderCounter = 0;

        List<CodeField.StringView> displayLines = textField.getDisplayLines();
        for (int i = 0; i < displayLines.size(); i++) {
            var stringView = displayLines.get(i);
            if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                cursorX = getX() + innerPadding() + lineNoWidth();
                if (stringView.beginIndex() != stringView.endIndex()) {
                    int lastEnd = stringView.beginIndex();
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
                // -------------------- render placeholders --------------------
                while (placeholderCounter < screen.getPlaceholders().size()) {
                    var placeholder = screen.getPlaceholders().get(placeholderCounter);
                    int place = placeholder.index();
                    if (place > stringView.endIndex())
                        break;
                    placeholderCounter++;
                    if (place < stringView.beginIndex())
                        continue;
                    int placeX = getX() + innerPadding() + lineNoWidth() +
                            font.width(content.substring(stringView.beginIndex(), place)) - 1;
                    guiGraphics.fill(placeX, currentY,
                            placeX + 1, currentY + font.lineHeight,
                            SnippetSet.contextColor(placeholder.context()));
                }
                // ----------------------- render cursor -----------------------
                boolean isCurLine = cursorInContent &&
                        cursor >= stringView.beginIndex() && cursor <= stringView.endIndex();
                if (isCurLine && isCursorRender) {
                    // cursor
                    cursorX = getX() + innerPadding() + lineNoWidth() +
                            font.width(content.substring(stringView.beginIndex(), cursor)) - 1;
                    guiGraphics.fill(cursorX, currentY,
                            cursorX + 1, currentY + font.lineHeight,
                            CURSOR_INSERT_COLOR);
                }
                // -------------------- render line numbers --------------------
                Component component = Component.literal(isContinuedLine ? ">" : String.valueOf(lineNo))
                        .withStyle(Style.EMPTY.withBold(isCurLine));
                guiGraphics.drawString(font, component,
                        getX() + lineNoWidth() - font.width("0")*component.getString().length(), currentY,
                        0xFF303030, false);
            }
            currentY += font.lineHeight;
            // it will always be (n, n) for the last line
            if (i != displayLines.size() - 1 && content.charAt(stringView.endIndex()) == '\n') {
                lineNo++;
                isContinuedLine = false;
            } else {
                isContinuedLine = true;
            }
        }

        int cursorY = currentY - font.lineHeight;
        if (isCursorRender && !cursorInContent &&
                withinContentAreaTopBottom(cursorY, cursorY + font.lineHeight)) {
            guiGraphics.drawString(font, CURSOR_APPEND_CHARACTER, cursorX, cursorY,
                    CURSOR_INSERT_COLOR, false);
        }

        if (textField.hasSelection()) {
            var selected = textField.getSelected();
            int k1 = getX() + innerPadding() + lineNoWidth();
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

    @SuppressWarnings("CommentedOutCode")
    protected void renderDecorations(@NotNull GuiGraphics guiGraphics) {
        super.renderDecorations(guiGraphics);
//        if (textField.hasCharacterLimit()) {
//            int i = textField.getCharacterLimit();
//            Component component = Component.translatable("gui.multiLineEditBox.character_limit", screen.getContent().length(), i);
//            guiGraphics.drawString(this.font, component, this.getX() + this.width - this.font.width(component), this.getY() + this.height + 4, 0xa0a0a0);
//        }
    }

    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.blit(BACKGROUND,
                getX() - outerPadding.left(), getY() - outerPadding.top(),
                0,  (int)scrollAmount(),
                width + outerPadding.horizontal(),
                height + outerPadding.vertical());
    }

    // scrolling
    public int getInnerHeight() {
        return font.lineHeight * textField.getDisplayLines().size();
    }

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return 3 * font.lineHeight;
    }
}
