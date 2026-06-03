package indi.wenyan.client.gui.code_editor.backend.behaviour;

import indi.wenyan.client.antlr.WenyanLexer;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum CodeFormatter {
    ;
    private static final Style CONTROL_STYLE = Style.EMPTY.withColor(0xFFB400);
    private static final Style STRING_STYLE = Style.EMPTY.withColor(0x008000);
    private static final Style DATA_STYLE = Style.EMPTY.withColor(0x1C00CF);
    private static final Style COMMENT_STYLE = Style.EMPTY.withColor(0xAAAAAA);
    private static final Style IDENTIFIER_STYLE = Style.EMPTY.withColor(0x005CC5);
    private static final Style OPERATOR_STYLE = Style.EMPTY.withColor(0xD73A49);
    private static final Style TYPE_STYLE = Style.EMPTY.withColor(0x795E26);
    private static final Style DEFAULT_STYLE = Style.EMPTY.withColor(0x000000);


    public static List<FormattedLine> splitLine(Font font, List<FormattedLine> code, int codeWidth) {
        List<FormattedLine> result = new ArrayList<>();
        for (var line : code) {
            if (font.width(line) < codeWidth) {
                result.add(line);
            } else {
                var lineCut = line;
                // logic here, because line.cut, cut self and return line after cutting point:
                // 1. find the first char that is out of codeWidth
                // 2. cut the line
                // 3. set the line to line after cutting point
                // 4. repeat until no need to cut
                while (font.width(lineCut) >= codeWidth) {
                    for (int i = 0; i < lineCut.length(); i++) {
                        if (lineCut.getWidth(font, i) >= codeWidth) {
                            result.add(lineCut); // changed after add to list
                            lineCut = lineCut.cut(i);
                            break;
                        }
                    }
                }
                result.add(lineCut);
            }
        }
        return result;
    }

    /// receive a code return the highlight format
    public static List<FormattedLine> highlightCode(String code) {
        // PLAN: hash/chunk the highlighting process to improve performance
        List<FormattedLine> result = new ArrayList<>();
        if (code.isEmpty()) {
            // early stop
            return result;
        }
        // split
        code = code + "\0"; // add a dummy \0 to adapt to mojang strange line view logic
        var lexer = new WenyanLexer(CharStreams.fromString(code));
        lexer.removeErrorListeners();
        var tokens = new BufferedTokenStream(lexer);
        StyledStringView currentToken = updateToken(tokens, 0);
        FormattedLine currentLine = new FormattedLine(0);
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (i > currentToken.endIndex()) {
                addToken(currentLine, code, currentToken);
                currentToken = updateToken(tokens, i);
            }
            if (c == '\n') {
                if (i < currentToken.endIndex) {
                    currentLine.add(code.substring(currentToken.beginIndex(), i + 1),
                            currentToken.tokenType);
                    currentToken = new StyledStringView(i + 1, currentToken.endIndex, currentToken.tokenType);
                } else { // also i == endIndex
                    addToken(currentLine, code, currentToken);
                    if (i != code.length() - 1)
                        currentToken = updateToken(tokens, i + 1);
                }
                result.add(currentLine);
                currentLine = new FormattedLine(i + 1);
            }
        }
        addToken(currentLine, code, currentToken);
        result.add(currentLine);
        return result;
    }

    private static @NotNull StyledStringView updateToken(BufferedTokenStream tokens, int i) {
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

    private static void addToken(FormattedLine formattedLine, String code, StyledStringView currentToken) {
        formattedLine.add(code.substring(currentToken.beginIndex(), currentToken.endIndex() + 1),
                currentToken.tokenType());
    }

    public static Style styleFromTokenType(int tokenType) {
        return switch (tokenType) {
            // control
            case WenyanLexer.RETURN_NULL, WenyanLexer.RETURN, WenyanLexer.RETURN_LAST,
                 WenyanLexer.BREAK_, WenyanLexer.CONTINUE_, WenyanLexer.IF_, WenyanLexer.ELSE_,
                 WenyanLexer.FOR_WHILE_SART, WenyanLexer.FOR_ARR_BELONG,
                 WenyanLexer.FOR_ENUM_START,
                 WenyanLexer.FOR_ARR_START, WenyanLexer.FOR_ENUM_TIMES, WenyanLexer.FOR_IF_END,
                 WenyanLexer.ZHE -> CONTROL_STYLE;
            // string
            case WenyanLexer.STRING_LITERAL -> STRING_STYLE;
            // data
            case WenyanLexer.FLOAT_NUM, WenyanLexer.INT_NUM, WenyanLexer.BOOL_VALUE -> DATA_STYLE;
            // comment
            case WenyanLexer.COMMENT -> COMMENT_STYLE;
            // identifier
            case WenyanLexer.IDENTIFIER, WenyanLexer.LONG, WenyanLexer.SELF, WenyanLexer.PARENT,
                 WenyanLexer.DATA_ID_LAST, WenyanLexer.ZHI -> IDENTIFIER_STYLE;
            // operator
            case WenyanLexer.ADD, WenyanLexer.SUB, WenyanLexer.MUL,
                 WenyanLexer.DIV, WenyanLexer.UNARY_OP, WenyanLexer.ARRAY_COMBINE_OP,
                 WenyanLexer.ARRAY_ADD_OP, WenyanLexer.WRITE_KEY_FUNCTION,
                 WenyanLexer.POST_MOD_MATH_OP,
                 WenyanLexer.AND, WenyanLexer.OR, WenyanLexer.NEQ, WenyanLexer.LTE,
                 WenyanLexer.GTE, WenyanLexer.EQ, WenyanLexer.GT, WenyanLexer.LT -> OPERATOR_STYLE;
            // type
            case WenyanLexer.BOOL_TYPE, WenyanLexer.STRING_TYPE, WenyanLexer.LIST_TYPE,
                 WenyanLexer.OBJECT_TYPE,
                 WenyanLexer.FUNCTION_TYPE, WenyanLexer.NUM_TYPE -> TYPE_STYLE;
            default -> DEFAULT_STYLE;
        };
    }


    private record StyledStringView(int beginIndex, int endIndex, int tokenType) {
    }
}
