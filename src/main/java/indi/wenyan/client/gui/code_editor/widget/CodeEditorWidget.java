package indi.wenyan.client.gui.code_editor.widget;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.gui.Utils;
import indi.wenyan.client.gui.code_editor.backend.CodeEditorBackend;
import indi.wenyan.client.gui.code_editor.backend.CodeField;
import indi.wenyan.client.gui.code_editor.backend.Completion;
import indi.wenyan.judou.antlr.WenyanRLexer;
import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
public class CodeEditorWidget extends AbstractTextAreaWidget {
    private static final int CURSOR_INSERT_COLOR = 0xff000000;
    public static final float TOOLTIP_SCALE = 0.7f;
    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
            "textures/gui/edit.png");
    // todo: make it larger (sprite)
    public static final int WIDTH = 256;
    public static final int HEIGH = 192;

    public static final int MAX_COMPLETION_CHAR = 16;
    public static final int MAX_RENDERED_COMPLETION_SIZE = 5;
    public static final int COMPLETION_SCROLL_WIDTH = 4;
    public static final int MAX_COMPLETION_WIDTH = 80;

    // NOTE: a minecraft inner padding of 4 is also need to be considered
    private static final int SCROLLBAR_WIDTH = 8;
    private static final Utils.BoxInformation outerPadding =
            new Utils.BoxInformation(4, 4, 4, 4 + SCROLLBAR_WIDTH);
    private static final Utils.BoxInformation completionPadding =
            new Utils.BoxInformation(1, 1, 1, 1);

    private final Font font;
    private long blinkStart = Util.getMillis(); // for blink

    private final CodeEditorBackend backend;

    @Getter
    private final CodeField textField;
    private List<Completion> completions = Collections.emptyList();
    private int firstCompletionLine = 0;
    private int selectedCompletion = 0;

    public CodeEditorWidget(Font font, CodeEditorBackend backend,
                            int x, int y, int width, int height) {
        super(x + outerPadding.left(), y + outerPadding.top(),
                width - outerPadding.horizontal(), height - outerPadding.vertical(),
                Component.empty(), AbstractTextAreaWidget.defaultSettings(3 * font.lineHeight));
        this.font = font;
        this.backend = backend;
        textField = new CodeField(font, backend,
                () -> this.width - totalInnerPadding() - lineNoWidth(),
                () -> {
                    scrollToCursor();
                    // reset blink
                    blinkStart = Util.getMillis();
                    completions = Collections.emptyList();
                    selectedCompletion = 0;
                    firstCompletionLine = 0;
                });
    }

    private int lineNoWidth() {
        // because the width of number is not same, return width of 0, 00, 000, ...
        // logic here: count of lines, get digit of this number, times width of "0"
        return font.width("0") * (String.valueOf(
                backend.getContent().chars().filter(c -> c == '\n').count() + 1).length() + 1) // +1 for wider number
                + innerPadding();
    }

    private void scrollToCursor() {
        double scrollAmount = scrollAmount();
        var displayLines = textField.getDisplayLines();

        int lineNo = Mth.clamp((int) (scrollAmount / font.lineHeight), 0,
                displayLines.size() - 1);
        int beginIndex = displayLines.get(lineNo).beginIndex();
        if (backend.getCursor() <= beginIndex) {
            scrollAmount = (double) textField.getLineAtCursor() * font.lineHeight;
        } else if ((int) ((scrollAmount + height) / font.lineHeight) - 1 < displayLines.size()) {
            int endIndex = displayLines.get((int) ((scrollAmount + height) / font.lineHeight) - 1).endIndex();
            if (backend.getCursor() > endIndex) {
                scrollAmount = (double) textField.getLineAtCursor() * font.lineHeight - height + font.lineHeight + totalInnerPadding();
            }
        }

        setScrollAmount(scrollAmount);
    }

    private static final Style CONTROL_STYLE = Style.EMPTY.withColor(0xFFB400);
    private static final Style STRING_STYLE = Style.EMPTY.withColor(0x008000);
    private static final Style DATA_STYLE = Style.EMPTY.withColor(0x1C00CF);
    private static final Style COMMENT_STYLE = Style.EMPTY.withColor(0xAAAAAA);
    private static final Style IDENTIFIER_STYLE = Style.EMPTY.withColor(0x005CC5);
    private static final Style OPERATOR_STYLE = Style.EMPTY.withColor(0xD73A49);
    private static final Style TYPE_STYLE = Style.EMPTY.withColor(0x795E26);
    private static final Style DEFAULT_STYLE = Style.EMPTY.withColor(0x000000);

    private static Style styleFromTokenType(int tokenType) {
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
		WS=78, NEWLINE=79
        */
        return switch (tokenType) {
            // control
            case WenyanRLexer.RETURN_NULL, WenyanRLexer.RETURN, WenyanRLexer.RETURN_LAST,
                 WenyanRLexer.BREAK_, WenyanRLexer.CONTINUE_, WenyanRLexer.IF_, WenyanRLexer.ELSE_,
                 WenyanRLexer.FOR_WHILE_SART, WenyanRLexer.FOR_ARR_BELONG,
                 WenyanRLexer.FOR_ENUM_START,
                 WenyanRLexer.FOR_ARR_START, WenyanRLexer.FOR_ENUM_TIMES, WenyanRLexer.FOR_IF_END,
                 WenyanRLexer.ZHE -> CONTROL_STYLE;
            // string
            case WenyanRLexer.STRING_LITERAL -> STRING_STYLE;
            // data
            case WenyanRLexer.FLOAT_NUM, WenyanRLexer.INT_NUM, WenyanRLexer.BOOL_VALUE ->
                    DATA_STYLE;
            // comment
            case WenyanRLexer.COMMENT -> COMMENT_STYLE;
            // identifier
            case WenyanRLexer.IDENTIFIER, WenyanRLexer.LONG, WenyanRLexer.SELF, WenyanRLexer.PARENT,
                 WenyanRLexer.DATA_ID_LAST, WenyanRLexer.ZHI -> IDENTIFIER_STYLE;
            // operator
            case WenyanRLexer.ADD, WenyanRLexer.SUB, WenyanRLexer.MUL,
                 WenyanRLexer.DIV, WenyanRLexer.UNARY_OP, WenyanRLexer.ARRAY_COMBINE_OP,
                 WenyanRLexer.ARRAY_ADD_OP, WenyanRLexer.WRITE_KEY_FUNCTION,
                 WenyanRLexer.POST_MOD_MATH_OP,
                 WenyanRLexer.AND, WenyanRLexer.OR, WenyanRLexer.NEQ, WenyanRLexer.LTE,
                 WenyanRLexer.GTE, WenyanRLexer.EQ, WenyanRLexer.GT, WenyanRLexer.LT ->
                    OPERATOR_STYLE;
            // type
            case WenyanRLexer.BOOL_TYPE, WenyanRLexer.STRING_TYPE, WenyanRLexer.LIST_TYPE,
                 WenyanRLexer.OBJECT_TYPE,
                 WenyanRLexer.FUNCTION_TYPE, WenyanRLexer.NUM_TYPE -> TYPE_STYLE;
            default -> DEFAULT_STYLE;
        };
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE,
                Component.translatable("gui.narrate.editBox", getMessage(), backend.getContent()));
    }

    // input
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        boolean result = false;
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            textField.setSelecting(event.hasShiftDown());
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
                    mouseY - getY() - innerPadding() + scrollAmount());
            result = true;
        }
        return super.mouseClicked(event, doubleClick) || result;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        boolean result = false;
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            textField.setSelecting(true);
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
                    mouseY - getY() - innerPadding() + scrollAmount());
            result = true;
        }
        return super.mouseDragged(event, dragX, dragY) || result;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (completions.isEmpty()) return textField.keyPressed(event);
        switch (event.key()) {
            case GLFW.GLFW_KEY_UP -> offsetSelectedCompletion(-1);
            case GLFW.GLFW_KEY_DOWN -> offsetSelectedCompletion(1);
            case GLFW.GLFW_KEY_ENTER -> {
                backend.setSelectCursor(findCompletionStart());
                backend.insertText(completions.get(selectedCompletion).content());
            }
            default -> {
                return textField.keyPressed(event);
            }
        }
        return true;
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        if (visible && isFocused() && StringUtil.isAllowedChatCharacter(event.codepoint())) {
            textField.insertText(Character.toString(event.codepoint()));
            completions = Completion.getCompletions(backend.getContent().substring(findCompletionStart(), backend.getCursor()));
            return true;
        } else {
            return false;
        }
    }

    private void offsetSelectedCompletion(int offset) {
        // get back if negative
        selectedCompletion = (selectedCompletion + offset) % completions.size();
        selectedCompletion = (selectedCompletion + completions.size()) % completions.size();
        if (firstCompletionLine > selectedCompletion) {
            firstCompletionLine = selectedCompletion;
        } else if (selectedCompletion >= firstCompletionLine + MAX_RENDERED_COMPLETION_SIZE) {
            firstCompletionLine = selectedCompletion - MAX_RENDERED_COMPLETION_SIZE + 1;
        }
    }

    private int findCompletionStart() {
        int completionStart = backend.getCursor();
        String content = backend.getContent();
        while (completionStart > Math.max(0, backend.getCursor() - MAX_COMPLETION_CHAR) &&
                Completion.isCharHandleable(content.charAt(completionStart - 1))) {
            completionStart--;
        }
        return completionStart;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            blinkStart = Util.getMillis();
        }
    }

    record CursorPosition(int x, int y) {
    }

    // rendering
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int cursorIndex = backend.getCursor();
        int currentY = getY() + innerPadding();
        int lineNo = 1;
        boolean isContinuedLine = false;
        var placeholderIter = backend.getPlaceholders().listIterator();
        @Nullable CursorPosition cursorPosition = null; // if null means cursor not within content area

        List<CodeField.StyledLineView> displayLines = textField.getDisplayLines();
        for (int i = 0; i < displayLines.size(); i++) {
            var stringView = displayLines.get(i);
            if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                renderStyledLine(guiGraphics, stringView, getX() + innerPadding() + lineNoWidth(), currentY);
                renderPlaceholders(guiGraphics, placeholderIter, stringView, currentY);
                // ----------------------- render cursor -----------------------
                boolean isCurLine = cursorIndex >= stringView.beginIndex() && cursorIndex <= stringView.endIndex();
                if (isCurLine) {
                    int cursorX = getX() + innerPadding() + lineNoWidth() +
                            font.width(backend.getContent().substring(stringView.beginIndex(), cursorIndex)) - 1;
                    boolean isCursorRender = isFocused() &&
                            (Util.getMillis() - blinkStart - 100L) / 500L % 2L == 0L;
                    renderCursor(guiGraphics, cursorX, currentY, isCursorRender);
                    cursorPosition = new CursorPosition(cursorX, currentY);
                }
                renderLineNumbers(guiGraphics, isContinuedLine, lineNo, isCurLine, currentY);
            }
            currentY += font.lineHeight;
            // it will always be (n, n) for the last line
            if (i != displayLines.size() - 1 && backend.getContent().charAt(stringView.endIndex()) == '\n') {
                lineNo++;
                isContinuedLine = false;
            } else {
                isContinuedLine = true;
            }
        }

        if (textField.hasSelection())
            renderSelection(guiGraphics);
        // render this as the last, overlap all above, as if it's floating no screen
        if (!completions.isEmpty() && cursorPosition != null)
            renderCompletion(guiGraphics, cursorPosition);
    }

    private void renderCursor(@NotNull GuiGraphics guiGraphics, int cursorX, int currentY, boolean isCursorRender) {
        // cursor
        if (isCursorRender) {
            guiGraphics.fill(cursorX, currentY,
                    cursorX + 1, currentY + font.lineHeight,
                    CURSOR_INSERT_COLOR);
        }
    }

    private void renderLineNumbers(@NotNull GuiGraphics guiGraphics, boolean isContinuedLine, int lineNo, boolean isCurLine, int currentY) {
        Component component = Component.literal(isContinuedLine ? ">" : String.valueOf(lineNo))
                .withStyle(Style.EMPTY.withBold(isCurLine));
        guiGraphics.drawString(font, component,
                getX() + lineNoWidth() - font.width("0") * component.getString().length(), currentY,
                0xFF303030, false);
    }

    private void renderStyledLine(@NotNull GuiGraphics guiGraphics, CodeField.StyledLineView stringView, int currentX, int currentY) {
        if (stringView.beginIndex() != stringView.endIndex()) {
            for (var styledView : stringView.styles()) {
                var style = styleFromTokenType(styledView.token());
                String tokenText = backend.getContent().substring(styledView.beginIndex(), styledView.endIndex());
                guiGraphics.drawString(font,
                        Component.literal(tokenText).withStyle(style),
                        currentX, currentY,
                        0xFFFFFFFF, false);
                currentX += font.width(Component.literal(tokenText).withStyle(style));
            }
        }
    }

    private void renderPlaceholders(@NotNull GuiGraphics guiGraphics, ListIterator<CodeField.Placeholder> placeholderIter, CodeField.StyledLineView stringView, int currentY) {
        while (placeholderIter.hasNext()) {
            var placeholder = placeholderIter.next();
            int place = placeholder.index();
            if (place > stringView.endIndex()) {
                placeholderIter.previous();
                break;
            }
            if (place >= stringView.beginIndex()) {
                int placeX = getX() + innerPadding() + lineNoWidth() +
                        font.width(backend.getContent().substring(stringView.beginIndex(), place)) - 1;
                guiGraphics.fill(placeX, currentY,
                        placeX + 1, currentY + font.lineHeight,
                        placeholder.context().getColor());
            }
        }
    }

    private void renderCompletion(@NotNull GuiGraphics guiGraphics, CursorPosition cursor) {
        final int entryHeight = font.lineHeight + completionPadding.vertical();
        final int renderedSize = Math.min(completions.size(), MAX_RENDERED_COMPLETION_SIZE);
        int w = completions.stream()
                .map(completion -> font.width(completion.content()) + completionPadding.horizontal())
                .reduce(50, Math::max) + COMPLETION_SCROLL_WIDTH;
        if (w > MAX_COMPLETION_WIDTH - COMPLETION_SCROLL_WIDTH)
            w = MAX_COMPLETION_WIDTH - COMPLETION_SCROLL_WIDTH;
        int tooltipHeight = (int) Math.ceil(font.lineHeight * TOOLTIP_SCALE);
        int h = entryHeight * renderedSize + tooltipHeight;
        // get x, y without exceed outline
        int x = Math.min(cursor.x(), getX() + this.width - w);
        int y = cursor.y() + font.lineHeight + h < getY() + this.height + scrollAmount() ?
                cursor.y() + font.lineHeight : cursor.y() - h;

        // render content
        guiGraphics.fill(x, y, x + w, y + h,
                0xffFFFFFF); // FIXME: change to a sprite
        guiGraphics.fill(x, y + (selectedCompletion - firstCompletionLine) * entryHeight,
                x + w, y + (selectedCompletion - firstCompletionLine + 1) * entryHeight,
                0xff99CCFF);
        int cnt = 0;
        for (int i = firstCompletionLine; i < firstCompletionLine + renderedSize; i++) {
            var completion = completions.get(i);
            String ellipsize = font.ellipsize(Component.literal(completion.content()), MAX_COMPLETION_WIDTH - COMPLETION_SCROLL_WIDTH).getString();
            guiGraphics.drawString(font, ellipsize,
                    x + completionPadding.left(),
                    y + (cnt++) * entryHeight + completionPadding.top(),
                    0xff000000, false);
        }

        // render scroll bar
        if (completions.size() > MAX_RENDERED_COMPLETION_SIZE) {
            guiGraphics.fill(x + w - COMPLETION_SCROLL_WIDTH, y,
                    x + w, y + h - tooltipHeight,
                    0xff000000);
            int scrollY = (h - tooltipHeight - 10) *
                    firstCompletionLine / (completions.size() - MAX_RENDERED_COMPLETION_SIZE);
            guiGraphics.fill(x + w - COMPLETION_SCROLL_WIDTH, y + scrollY,
                    x + w, y + scrollY + 10,
                    0xffCCCCCC);
        }

        // render tooltip
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((float) x + completionPadding.left(), (float) y + entryHeight * renderedSize);
        guiGraphics.pose().scale(TOOLTIP_SCALE, TOOLTIP_SCALE);
        guiGraphics.drawString(font, Component.literal("Enter to input"),
                0, 0, // position handled by pose
                0xff999999, false);
        guiGraphics.pose().popMatrix();
    }

    private void renderSelection(@NotNull GuiGraphics guiGraphics) {
        var selected = textField.getSelected();
        int k1 = getX() + innerPadding() + lineNoWidth();
        int currentY = getY() + innerPadding();

        for (var stringView : textField.getDisplayLines()) {
            if (selected.beginIndex() <= stringView.endIndex()) {
                if (stringView.beginIndex() > selected.endIndex()) {
                    break;
                }

                if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                    int i1 = font.width(backend.getContent().substring(stringView.beginIndex(), Math.max(selected.beginIndex(), stringView.beginIndex())));
                    int j1;
                    if (selected.endIndex() > stringView.endIndex()) {
                        j1 = width - innerPadding();
                    } else {
                        j1 = font.width(backend.getContent().substring(stringView.beginIndex(), selected.endIndex()));
                    }
                    guiGraphics.textHighlight(
                            k1 + i1, currentY, k1 + j1, currentY + font.lineHeight, true);
                }
            }
            currentY += font.lineHeight;
        }
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                getX() - outerPadding.left(), getY() - outerPadding.top(),
                0, (int) scrollAmount(),
                width + outerPadding.horizontal(),
                height + outerPadding.vertical(),
                WIDTH, HEIGH);
    }

    // scrolling
    public int getInnerHeight() {
        return font.lineHeight * textField.getDisplayLines().size();
    }
}
