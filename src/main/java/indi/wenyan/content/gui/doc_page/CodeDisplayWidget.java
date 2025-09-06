package indi.wenyan.content.gui.doc_page;

import indi.wenyan.content.gui.code_editor.CodeField;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
@OnlyIn(Dist.CLIENT)
public class CodeDisplayWidget {
    private final Font font;

    private final CodeField.SavedVariable screen;

    private final CodeField textField;

    @Getter
    private final int x, y;

    private static final int PADDING = 4;

    public CodeDisplayWidget(Font font, CodeField.SavedVariable screen,
                             int x, int y, int width, int ignoredHeight) {
        this.x = x;
        this.y = y;

        this.font = font;
        this.screen = screen;
        textField = new CodeField(font, screen, width);
        // notify change
        textField.setWidthUpdater(this::lineNoWidth);
    }

    private int lineNoWidth() {
        // because the width of number is not same, return width of 0, 00, 000, ...
        // logic here: count of lines, get digit of this number, times width of "0"
        return font.width("0") * ((int) Math.log10(
                screen.getContent().chars().filter(c -> c == '\n').count() + 1) + 1) + PADDING;
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

    // rendering
    protected void render(@NotNull GuiGraphics guiGraphics) {
        StringBuilder content = screen.getContent();
        int cursorX;
        int currentY = getY();
        int lineNo = 1;
        boolean isContinuedLine = false;
        int styleCounter = 0;

        List<CodeField.StringView> displayLines = textField.getDisplayLines();
        for (int i = 0; i < displayLines.size(); i++) {
            var stringView = displayLines.get(i);
            cursorX = getX() + lineNoWidth() + PADDING;
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
            // -------------------- render line numbers --------------------
            Component component = Component.literal(isContinuedLine ? ">" : String.valueOf(lineNo));
            guiGraphics.drawString(font, component,
                    getX() + lineNoWidth() - font.width("0")*component.getString().length(), currentY,
                    0xFF303030, false);
            currentY += font.lineHeight;
            // it will always be (n, n) for the last line
            if (i != displayLines.size() - 1 && content.charAt(stringView.endIndex()) == '\n') {
                lineNo++;
                isContinuedLine = false;
            } else {
                isContinuedLine = true;
            }
        }
    }
}
