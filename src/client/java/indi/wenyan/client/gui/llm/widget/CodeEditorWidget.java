package indi.wenyan.client.gui.llm.widget;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.gui.Utils;
import indi.wenyan.client.gui.code_editor.backend.behaviour.Completion;
import indi.wenyan.client.gui.code_editor.backend.behaviour.SnippetSet;
import indi.wenyan.client.gui.code_editor.backend.behaviour.generated_Snippets;
import indi.wenyan.client.gui.code_editor.backend.interfaces.CodeEditBackend;
import indi.wenyan.client.gui.llm.backend.CodeField;
import indi.wenyan.client.gui.llm.backend.llm.LlmCodeDiff;
import indi.wenyan.judou.antlr.WenyanLexer;
import indi.wenyan.setup.language.GuiText;
import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

// copy from net.minecraft.client.gui.components.MultiLineEditBox
public class CodeEditorWidget extends AbstractTextAreaWidget {
    // todo: make it larger (sprite)
    public static final int WIDTH = 256;
    public static final int HEIGH = 192;

    private static final int CURSOR_INSERT_COLOR = 0xff000000;
    private static final int LINE_NUM_COLOR = 0xFF303030;
    private static final int PURE_WHITE = 0xFFFFFFFF;
    private static final int COMPLETION_SELECTED = 0xff99CCFF;
    private static final int COMPLETION_BACKGROUND = 0xFFFFFFFF;
    private static final int COMPLETION_TEXT_COLOR = 0xff000000;
    private static final int COMPLETION_SCROLL_BACKGROUND = 0xff000000;
    private static final int COMPLETION_SCROLL_FOREGROUND = 0xffCCCCCC;
    private static final int TOOLTIP_TEXT_COLOR = 0xff999999;
    private static final int PREVIEW_ADDED_BACKGROUND = 0x5533C75A;
    private static final int PREVIEW_REMOVED_BACKGROUND = 0x55FF4D4D;
    private static final int PREVIEW_ADDED_GUTTER = 0xFF238636;
    private static final int PREVIEW_REMOVED_GUTTER = 0xFFB42323;

    private static final Style CONTROL_STYLE = Style.EMPTY.withColor(0xFFB400);
    private static final Style STRING_STYLE = Style.EMPTY.withColor(0x008000);
    private static final Style DATA_STYLE = Style.EMPTY.withColor(0x1C00CF);
    private static final Style COMMENT_STYLE = Style.EMPTY.withColor(0xAAAAAA);
    private static final Style IDENTIFIER_STYLE = Style.EMPTY.withColor(0x005CC5);
    private static final Style OPERATOR_STYLE = Style.EMPTY.withColor(0xD73A49);
    private static final Style TYPE_STYLE = Style.EMPTY.withColor(0x795E26);
    private static final Style DEFAULT_STYLE = Style.EMPTY.withColor(0x000000);

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(WenyanProgramming.MODID,
            "textures/gui/edit.png");

    private static final int MAX_COMPLETION_CHAR = 16;
    private static final int MAX_RENDERED_COMPLETION_SIZE = 5;
    private static final int MAX_COMPLETION_WIDTH = 80;
    private static final float TOOLTIP_SCALE = 0.7f;
    private static final int COMPLETION_SCROLL_WIDTH = 4;

    // NOTE: a minecraft inner padding of 4 is also need to be considered
    private static final int SCROLLBAR_WIDTH = 8;
    private static final Utils.BoxInformation outerPadding =
            new Utils.BoxInformation(4, 4, 4, 4 + SCROLLBAR_WIDTH);
    private static final Utils.BoxInformation completionPadding =
            new Utils.BoxInformation(1, 1, 1, 1);

    private static final String SPLIT_LINE_MARK = ">";

    private final Font font;
    private long blinkStart = Util.getMillis(); // for blink

    private final CodeEditBackend backend;

    @Getter
    private final CodeField textField;
    private final PreviewBackend previewBackend = new PreviewBackend();
    private final CodeField previewTextField;
    private final List<PreviewRange> previewRanges = new ArrayList<>();
    @Getter private boolean llmPreviewing = false;
    private List<Completion> completions = Collections.emptyList();
    private int firstCompletionLine = 0;
    private int selectedCompletion = 0;

    public CodeEditorWidget(Font font, CodeEditBackend backend,
                            int x, int y, int width, int height) {
        super(x + outerPadding.left(), y + outerPadding.top(),
                width - outerPadding.horizontal(), height - outerPadding.vertical(),
                Component.empty(), AbstractScrollArea.defaultSettings(3 * font.lineHeight));
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
        previewTextField = new CodeField(font, previewBackend,
                () -> this.width - totalInnerPadding() - lineNoWidth(),
                () -> setScrollAmount(0));
    }

    private int lineNoWidth() {
        // because the width of number is not same, return width of 0, 00, 000, ...
        // logic here: count of lines, get digit of this number, times width of "0"
        return font.width("0") * (String.valueOf(
                getRenderedContent().chars().filter(c -> c == '\n').count() + 1).length() + 1) // +1 for wider number
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

    private static Style styleFromTokenType(int tokenType) {
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
            case WenyanLexer.FLOAT_NUM, WenyanLexer.INT_NUM, WenyanLexer.BOOL_VALUE ->
                    DATA_STYLE;
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
                 WenyanLexer.GTE, WenyanLexer.EQ, WenyanLexer.GT, WenyanLexer.LT ->
                    OPERATOR_STYLE;
            // type
            case WenyanLexer.BOOL_TYPE, WenyanLexer.STRING_TYPE, WenyanLexer.LIST_TYPE,
                 WenyanLexer.OBJECT_TYPE,
                 WenyanLexer.FUNCTION_TYPE, WenyanLexer.NUM_TYPE -> TYPE_STYLE;
            default -> DEFAULT_STYLE;
        };
    }

    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, GuiText.NarrateEditBox.text());
    }

    // input
    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (llmPreviewing)
            return false;
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
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dragX, double dragY) {
        if (llmPreviewing)
            return false;
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
        if (llmPreviewing)
            return false;
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
        if (llmPreviewing)
            return false;
        if (visible && isFocused() && StringUtil.isAllowedChatCharacter(event.codepoint())) {
            backend.insertText(Character.toString(event.codepoint()));
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
    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        CodeField renderedTextField = getRenderedTextField();
        String renderedContent = getRenderedContent();
        int cursorIndex = backend.getCursor();
        int currentY = getY() + innerPadding();
        int lineNo = 1;
        boolean isContinuedLine = false;
        var placeholderIter = llmPreviewing ? Collections.<indi.wenyan.client.gui.code_editor.backend.behaviour.CodeField.Placeholder>emptyList().listIterator() :
                backend.getPlaceholders().listIterator();
        @Nullable CursorPosition cursorPosition = null; // if null means cursor not within content area

        List<CodeField.StyledLineView> displayLines = renderedTextField.getDisplayLines();
        for (int i = 0; i < displayLines.size(); i++) {
            var stringView = displayLines.get(i);
            if (withinContentAreaTopBottom(currentY, currentY + font.lineHeight)) {
                renderPreviewBackground(guiGraphics, stringView, currentY);
                renderStyledLine(guiGraphics, renderedContent, stringView, getX() + innerPadding() + lineNoWidth(), currentY);
                if (!llmPreviewing) {
                    renderPlaceholders(guiGraphics, placeholderIter, stringView, currentY);
                }
                // ----------------------- render cursor -----------------------
                boolean isCurLine = !llmPreviewing && cursorIndex >= stringView.beginIndex() && cursorIndex <= stringView.endIndex();
                if (isCurLine) {
                    int cursorX = getX() + innerPadding() + lineNoWidth() +
                            font.width(renderedContent.substring(stringView.beginIndex(), cursorIndex)) - 1;
                    boolean isCursorRender = isFocused() && isBlinkShow();
                    renderCursor(guiGraphics, cursorX, currentY, isCursorRender);
                    cursorPosition = new CursorPosition(cursorX, currentY);
                }
                renderLineNumbers(guiGraphics, isContinuedLine, lineNo, isCurLine, currentY);
            }
            currentY += font.lineHeight;
            // it will always be (n, n) for the last line
            if (i != displayLines.size() - 1 && renderedContent.charAt(stringView.endIndex()) == '\n') {
                lineNo++;
                isContinuedLine = false;
            } else {
                isContinuedLine = true;
            }
        }

        if (!llmPreviewing && textField.hasSelection())
            renderSelection(guiGraphics);
        // render this as the last, overlap all above, as if it's floating no screen
        if (!completions.isEmpty() && cursorPosition != null)
            renderCompletion(guiGraphics, cursorPosition);
    }

    private boolean isBlinkShow() {
        return (Util.getMillis() - blinkStart - 100L) / 500L % 2L == 0L;
    }

    private void renderCursor(@NotNull GuiGraphicsExtractor guiGraphics, int cursorX, int currentY, boolean isCursorRender) {
        final int cursorWidth = 1;
        // cursor
        if (isCursorRender) {
            guiGraphics.fill(cursorX, currentY,
                    cursorX + cursorWidth, currentY + font.lineHeight,
                    CURSOR_INSERT_COLOR);
        }
    }

    private void renderLineNumbers(@NotNull GuiGraphicsExtractor guiGraphics, boolean isContinuedLine, int lineNo, boolean isCurLine, int currentY) {
        Component component = Component.literal(isContinuedLine ? SPLIT_LINE_MARK : String.valueOf(lineNo))
                .withStyle(Style.EMPTY.withBold(isCurLine));
        guiGraphics.text(font, component,
                getX() + lineNoWidth() - font.width(component), currentY,
                LINE_NUM_COLOR, false);
    }

    private void renderPreviewBackground(@NotNull GuiGraphicsExtractor guiGraphics, CodeField.StyledLineView stringView, int currentY) {
        if (!llmPreviewing)
            return;
        LlmCodeDiff.Type type = getPreviewType(stringView.beginIndex());
        int color = switch (type) {
            case ADDED -> PREVIEW_ADDED_BACKGROUND;
            case REMOVED -> PREVIEW_REMOVED_BACKGROUND;
            case UNCHANGED -> 0x00000000;
        };
        if (color != 0x00000000) {
            guiGraphics.fill(getX() + innerPadding(), currentY,
                    getX() + getWidth() - innerPadding(), currentY + font.lineHeight, color);
        }
        switch (type) {
            case ADDED -> guiGraphics.fill(getX() + innerPadding(), currentY,
                    getX() + innerPadding() + 2, currentY + font.lineHeight, PREVIEW_ADDED_GUTTER);
            case REMOVED -> guiGraphics.fill(getX() + innerPadding(), currentY,
                    getX() + innerPadding() + 2, currentY + font.lineHeight, PREVIEW_REMOVED_GUTTER);
            case UNCHANGED -> {
            }
        }
    }

    private void renderStyledLine(@NotNull GuiGraphicsExtractor guiGraphics, String content,
                                  CodeField.StyledLineView stringView, int currentX, int currentY) {
        if (stringView.beginIndex() != stringView.endIndex()) {
            for (var styledView : stringView.styles()) {
                var style = styleFromTokenType(styledView.token());
                String tokenText = content.substring(styledView.beginIndex(), styledView.endIndex());
                guiGraphics.text(font,
                        Component.literal(tokenText).withStyle(style),
                        currentX, currentY,
                        PURE_WHITE, false);
                currentX += font.width(Component.literal(tokenText).withStyle(style));
            }
        }
    }

    private void renderPlaceholders(@NotNull GuiGraphicsExtractor guiGraphics, ListIterator<indi.wenyan.client.gui.code_editor.backend.behaviour.CodeField.Placeholder> placeholderIter, CodeField.StyledLineView stringView, int currentY) {
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

    private void renderCompletion(@NotNull GuiGraphicsExtractor guiGraphics, CursorPosition cursor) {
        final int entryHeight = font.lineHeight + completionPadding.vertical();
        final int renderedSize = Math.min(completions.size(), MAX_RENDERED_COMPLETION_SIZE);
        final int scrollBarHeight = 10;
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
                COMPLETION_BACKGROUND); // FIXME: change to a sprite
        guiGraphics.fill(x, y + (selectedCompletion - firstCompletionLine) * entryHeight,
                x + w, y + (selectedCompletion - firstCompletionLine + 1) * entryHeight,
                COMPLETION_SELECTED);
        int cnt = 0;
        for (int i = firstCompletionLine; i < firstCompletionLine + renderedSize; i++) {
            var completion = completions.get(i);
            String ellipsize = font.ellipsize(Component.literal(completion.content()), MAX_COMPLETION_WIDTH - COMPLETION_SCROLL_WIDTH).getString();
            guiGraphics.text(font, ellipsize,
                    x + completionPadding.left(),
                    y + (cnt++) * entryHeight + completionPadding.top(),
                    COMPLETION_TEXT_COLOR, false);
        }

        // render scroll bar
        if (completions.size() > MAX_RENDERED_COMPLETION_SIZE) {
            guiGraphics.fill(x + w - COMPLETION_SCROLL_WIDTH, y,
                    x + w, y + h - tooltipHeight,
                    COMPLETION_SCROLL_BACKGROUND);
            int scrollY = (h - tooltipHeight - scrollBarHeight) *
                    firstCompletionLine / (completions.size() - MAX_RENDERED_COMPLETION_SIZE);
            guiGraphics.fill(x + w - COMPLETION_SCROLL_WIDTH, y + scrollY,
                    x + w, y + scrollY + scrollBarHeight,
                    COMPLETION_SCROLL_FOREGROUND);
        }

        // render tooltip
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate((float) x + completionPadding.left(), (float) y + entryHeight * renderedSize);
        guiGraphics.pose().scale(TOOLTIP_SCALE, TOOLTIP_SCALE);
        guiGraphics.text(font, GuiText.EnterToInput.text(),
                0, 0, // position handled by pose
                TOOLTIP_TEXT_COLOR, false);
        guiGraphics.pose().popMatrix();
    }

    private void renderSelection(@NotNull GuiGraphicsExtractor guiGraphics) {
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
    protected void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics) {
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
        return font.lineHeight * getRenderedTextField().getDisplayLines().size();
    }

    public void showLlmPreview(String previewCode, List<LlmCodeDiff.Line> diff) {
        previewRanges.clear();
        StringBuilder content = new StringBuilder();
        List<LlmCodeDiff.Line> lines = diff.isEmpty() ? LlmCodeDiff.diff("", previewCode) : diff;
        for (int i = 0; i < lines.size(); i++) {
            LlmCodeDiff.Line line = lines.get(i);
            int beginIndex = content.length();
            content.append(line.text());
            int endIndex = content.length();
            previewRanges.add(new PreviewRange(beginIndex, endIndex, line.type()));
            if (i != lines.size() - 1) {
                content.append('\n');
            }
        }
        previewBackend.setContent(content.toString());
        llmPreviewing = true;
        completions = Collections.emptyList();
        setScrollAmount(0);
    }

    public void clearLlmPreview() {
        llmPreviewing = false;
        previewRanges.clear();
        previewBackend.setContent("");
        setScrollAmount(0);
    }

    private CodeField getRenderedTextField() {
        return llmPreviewing ? previewTextField : textField;
    }

    private String getRenderedContent() {
        return llmPreviewing ? previewBackend.getContent() : backend.getContent();
    }

    private LlmCodeDiff.Type getPreviewType(int index) {
        int low = 0;
        int high = previewRanges.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            PreviewRange range = previewRanges.get(mid);
            if (index < range.beginIndex()) {
                high = mid - 1;
            } else if (index > range.endIndex()) {
                low = mid + 1;
            } else {
                return range.type();
            }
        }
        return LlmCodeDiff.Type.UNCHANGED;
    }

    private record PreviewRange(int beginIndex, int endIndex, LlmCodeDiff.Type type) {
    }

    private static class PreviewBackend implements CodeEditBackend {
        private String content = "";
        private Runnable valueListener = () -> {
        };

        public void setContent(String content) {
            this.content = content;
            valueListener.run();
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public List<indi.wenyan.client.gui.code_editor.backend.behaviour.CodeField.Placeholder> getPlaceholders() {
            return List.of();
        }

        @Override
        public void insertText(String text) {
        }

        @Override
        public void setCursor(int cursor) {
        }

        @Override
        public void setSelectCursor(int selectCursor) {
        }

        @Override
        public int getCursor() {
            return 0;
        }

        @Override
        public int getSelectCursor() {
            return 0;
        }

        @Override
        public List<SnippetSet> getCurSnippets() {
            return generated_Snippets.DEFAULT_CONTEXT;
        }

        @Override
        public void setCurSnippets(List<SnippetSet> curSnippets) {
        }

        @Override
        public void setCursorListener(Runnable cursorListener) {
        }

        @Override
        public void setValueListener(Runnable valueListener) {
            this.valueListener = valueListener;
        }
    }
}
