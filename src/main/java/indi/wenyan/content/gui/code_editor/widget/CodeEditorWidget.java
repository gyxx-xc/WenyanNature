package indi.wenyan.content.gui.code_editor.widget;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.gui.Utils;
import indi.wenyan.content.gui.code_editor.backend.CodeEditorBackend;
import indi.wenyan.content.gui.code_editor.backend.CodeField;
import indi.wenyan.content.gui.code_editor.backend.Completion;
import indi.wenyan.interpreter.antlr.WenyanRLexer;
import lombok.Getter;
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
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
@OnlyIn(Dist.CLIENT)
public class CodeEditorWidget extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_COLOR = 0xff000000;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    public static final float TOOLTIP_SCALE = 0.7f;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID,
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
                Component.empty());
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            textField.setSelecting(Screen.hasShiftDown());
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
                    mouseY - getY() - innerPadding() + scrollAmount());
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        } else if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            textField.setSelecting(true);
            textField.seekCursorToPoint(mouseX - getX() - innerPadding() - lineNoWidth(),
                    mouseY - getY() - innerPadding() + scrollAmount());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (completions.isEmpty()) return textField.keyPressed(keyCode);
        switch (keyCode) {
            case GLFW.GLFW_KEY_UP -> offsetSelectedCompletion(-1);
            case GLFW.GLFW_KEY_DOWN -> offsetSelectedCompletion(1);
            case GLFW.GLFW_KEY_ENTER -> {
                backend.setSelectCursor(findCompletionStart());
                backend.insertText(completions.get(selectedCompletion).content());
            }
            default -> {
                return textField.keyPressed(keyCode);
            }
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (visible && isFocused() && StringUtil.isAllowedChatCharacter(codePoint)) {
            textField.insertText(Character.toString(codePoint));
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

    // rendering
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int cursor = backend.getCursor();
        boolean isCursorRender = !isFocused() ||
                (Util.getMillis() - blinkStart - 100L) / 500L % 2L == 0L;
        boolean cursorInContent = cursor < backend.getContent().length();
        int currentX = 0;
        int currentY = getY() + innerPadding();
        int lineNo = 1;
        boolean isContinuedLine = false;
        int styleCounter = 0;
        int placeholderCounter = 0;
        int cursorX = 0;
        int cursorY = getY() + innerPadding();

        List<CodeField.StringView> displayLines = textField.getDisplayLines();
        for (int i = 0; i < displayLines.size(); i++) {
            var stringView = displayLines.get(i);
            if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                currentX = getX() + innerPadding() + lineNoWidth();
                if (stringView.beginIndex() != stringView.endIndex()) {
                    int lastEnd = stringView.beginIndex();
                    do {
                        int end;
                        do {
                            end = textField.getStyleMarks().get(styleCounter).endIndex();
                        } while (end <= stringView.beginIndex() && ++styleCounter < textField.getStyleMarks().size());
                        String line = backend.getContent().substring(lastEnd,
                                Math.clamp(end, stringView.beginIndex(), stringView.endIndex()));
                        var style = styleFromTokenType(textField.getStyleMarks().get(styleCounter).style());
                        currentX = guiGraphics.drawString(font, Component.literal(line).withStyle(style),
                                currentX, currentY,
                                0xFFFFFFFF, false);
                        lastEnd = end;
                        if (end < stringView.endIndex()) {
                            styleCounter++;
                        }
                    } while (lastEnd < stringView.endIndex());
                }
                // -------------------- render placeholders --------------------
                while (placeholderCounter < backend.getPlaceholders().size()) {
                    var placeholder = backend.getPlaceholders().get(placeholderCounter);
                    int place = placeholder.index();
                    if (place > stringView.endIndex())
                        break;
                    placeholderCounter++;
                    if (place >= stringView.beginIndex()) {
                        int placeX = getX() + innerPadding() + lineNoWidth() +
                                font.width(backend.getContent().substring(stringView.beginIndex(), place)) - 1;
                        guiGraphics.fill(placeX, currentY,
                                placeX + 1, currentY + font.lineHeight,
                                placeholder.context().getColor());
                    }
                }
                // ----------------------- render cursor -----------------------
                boolean isCurLine = cursorInContent &&
                        cursor >= stringView.beginIndex() && cursor <= stringView.endIndex();
                if (isCurLine) {
                    // cursor
                    currentX = getX() + innerPadding() + lineNoWidth() +
                            font.width(backend.getContent().substring(stringView.beginIndex(), cursor)) - 1;
                    cursorY = currentY;
                    cursorX = currentX;
                    if (isCursorRender) {
                        guiGraphics.fill(cursorX, cursorY,
                                cursorX + 1, cursorY + font.lineHeight,
                                CURSOR_INSERT_COLOR);
                    }
                }
                // -------------------- render line numbers --------------------
                Component component = Component.literal(isContinuedLine ? ">" : String.valueOf(lineNo))
                        .withStyle(Style.EMPTY.withBold(isCurLine));
                guiGraphics.drawString(font, component,
                        getX() + lineNoWidth() - font.width("0") * component.getString().length(), currentY,
                        0xFF303030, false);
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

        if (!cursorInContent && withinContentAreaTopBottom(currentY - font.lineHeight, currentY)) {
            cursorY = currentY - font.lineHeight;
            cursorX = currentX;
            if (isCursorRender) {
                guiGraphics.drawString(font, CURSOR_APPEND_CHARACTER, cursorX, cursorY,
                        CURSOR_INSERT_COLOR, false);
            }
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
                        int i1 = font.width(backend.getContent().substring(stringView.beginIndex(), Math.max(selected.beginIndex(), stringView.beginIndex())));
                        int j1;
                        if (selected.endIndex() > stringView.endIndex()) {
                            j1 = width - innerPadding();
                        } else {
                            j1 = font.width(backend.getContent().substring(stringView.beginIndex(), selected.endIndex()));
                        }
                        guiGraphics.fill(RenderType.guiTextHighlight(),
                                k1 + i1, currentY, k1 + j1, currentY + font.lineHeight,
                                0xff0000ff);
                    }
                }
                currentY += font.lineHeight;
            }
        }
        // render this as the last, overlap all above, as if it's floating no screen
        if (!completions.isEmpty() && withinContentAreaTopBottom(cursorY, cursorY + font.lineHeight)) {
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
            int x = Math.min(cursorX, getX() + this.width - w);
            int y = cursorY + font.lineHeight + h < getY() + this.height + scrollAmount() ?
                    cursorY + font.lineHeight : cursorY - h;

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
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float) x + completionPadding.left(), (float) y + entryHeight * renderedSize, 0);
            guiGraphics.pose().scale(TOOLTIP_SCALE, TOOLTIP_SCALE, 1.0f);
            guiGraphics.drawString(font, Component.literal("Enter to input"),
                    0, 0, // position handled by pose
                    0xff999999, false);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {
        guiGraphics.blit(BACKGROUND,
                getX() - outerPadding.left(), getY() - outerPadding.top(),
                0, (int) scrollAmount(),
                width + outerPadding.horizontal(),
                height + outerPadding.vertical());
    }

    // scrolling
    public int getInnerHeight() {
        return font.lineHeight * textField.getDisplayLines().size();
    }

    @Override
    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return (double) 3 * font.lineHeight;
    }
}
