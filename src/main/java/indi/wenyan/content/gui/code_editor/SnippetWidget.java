package indi.wenyan.content.gui.code_editor;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.gui.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SnippetWidget extends AbstractScrollWidget {
    private final Font font;
    private final CodeEditorBackend backend;

    public static final WidgetSprites ENTRY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_highlighted"));
    public static final WidgetSprites DIR_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_dir"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_dir_folded"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_dir_highlighted"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_dir_folded_highlighted"));

    private static final Utils.BoxInformation buttonPadding =
            new Utils.BoxInformation(3, 3, 3, 3);
    private static final Utils.BoxInformation entryPadding =
            new Utils.BoxInformation(3, 3 + 9, 3, 3);
    public static final int ENTRY_HEIGHT = 9 + buttonPadding.vertical();
    public static final int DIR_HEIGHT = 9 + buttonPadding.vertical();

    @Nullable
    private SnippetSet.Snippet renderingSnippetTooltip = null;

    public Optional<SnippetSet.Snippet> getRenderingSnippetTooltip() {
        return Optional.ofNullable(renderingSnippetTooltip);
    }

    public SnippetWidget(Font font, CodeEditorBackend backend, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.backend = backend;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int currentY = getY() + innerPadding();
        setScrollAmount(scrollAmount()); // clamp scroll amount
        int offsetMouseY = mouseY + (int) scrollAmount();
        renderingSnippetTooltip = null;
        for (SnippetSet set : backend.getCurSnippets()) {
            boolean singleEntryDir = set.snippets().size() == 1;
            if (singleEntryDir) {
                renderEntry(guiGraphics, mouseX, offsetMouseY, currentY, set.name(), set.snippets().getFirst(), set.fold());
            } else {
                renderDir(guiGraphics, mouseX, offsetMouseY, currentY, set.name(), set.fold());
            }
            currentY += DIR_HEIGHT;
            // if only one snippet, dir is the snippet
            // and skip snippets in this set if folded
            if (singleEntryDir || set.fold()) continue;

            for (SnippetSet.Snippet s : set.snippets()) {
                renderEntry(guiGraphics, mouseX, offsetMouseY, currentY, s.title(), s, true);
                currentY += ENTRY_HEIGHT;
            }
        }
    }

    private void renderEntry(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int currentY,
                           String title, @Nullable SnippetSet.Snippet tooltip, boolean isUnfold) {
        if (withinContentAreaTopBottom(currentY, currentY + DIR_HEIGHT)) {
            boolean buttonHovered = mouseX >= getX() + innerPadding() &&
                    mouseX < getX() + getWidth() - innerPadding() &&
                    mouseY >= currentY && mouseY < currentY + DIR_HEIGHT;
            guiGraphics.blitSprite(
                    ENTRY_SPRITES.get(isUnfold, buttonHovered),
                    getX() + innerPadding(), currentY,
                    this.getWidth() - totalInnerPadding(), DIR_HEIGHT
            );

            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(title),
                            width - totalInnerPadding() - entryPadding.horizontal()));
            guiGraphics.drawString(font, text,
                    getX() + innerPadding() + entryPadding.left(), currentY + entryPadding.top(),
                    0xFFFFFF, false);

            if (buttonHovered) {
                renderingSnippetTooltip = tooltip;
            }
        }
    }

    private void renderDir(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int currentY,
                         String title, boolean isUnfold) {
        if (withinContentAreaTopBottom(currentY, currentY + DIR_HEIGHT)) {
            boolean buttonHovered = mouseX >= getX() + innerPadding() &&
                    mouseX < getX() + getWidth() - innerPadding() &&
                    mouseY >= currentY && mouseY < currentY + DIR_HEIGHT;
            guiGraphics.blitSprite(
                    DIR_SPRITES.get(isUnfold, buttonHovered),
                    getX() + innerPadding(), currentY,
                    this.getWidth() - totalInnerPadding(), DIR_HEIGHT
            );

            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(title),
                            width - totalInnerPadding() - buttonPadding.horizontal()));
            guiGraphics.drawString(font, text,
                    getX() + innerPadding() + buttonPadding.left(), currentY + buttonPadding.top(),
                    0xFFFFFF, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            double y = mouseY - getY() - innerPadding() + scrollAmount();
            double currentY = 0;
            for (SnippetSet set : backend.getCurSnippets()) {
                if (y >= currentY && y < currentY + DIR_HEIGHT) {
                    // clicked on directory
                    if (set.snippets().size() == 1) { // if only one snippet, dir is the snippet
                        insertSnippet(set.snippets().getFirst());
                    } else {
                        set.fold(!set.fold());
                    }
                    return true;
                }
                currentY += DIR_HEIGHT;
                if (set.snippets().size() == 1 || set.fold()) continue;
                for (SnippetSet.Snippet s : set.snippets()) {
                    if (y >= currentY && y < currentY + ENTRY_HEIGHT) {
                        // clicked on snippet
                        insertSnippet(s);
                        return true;
                    }
                    currentY += ENTRY_HEIGHT;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void insertSnippet(SnippetSet.Snippet snippet) {
        // get indent
        StringBuilder indent = new StringBuilder();
        if (backend.getCursor() > 0) {
            int lastNewline = backend.getContent().lastIndexOf("\n", backend.getCursor() - 1);
            int firstChar;
            if (lastNewline >= 0)
                firstChar = lastNewline + 1;
            else // first line
                firstChar = 0;
            while (firstChar < backend.getCursor() && Character.isWhitespace(backend.getContent().charAt(firstChar))) {
                indent.append(backend.getContent().charAt(firstChar));
                firstChar++;
            }
        }
        StringBuilder sb = new StringBuilder();
        int start = Math.min(backend.getSelectCursor(), backend.getCursor());
        List<String> lines = snippet.lines();
        // j for placeholders
        List<CodeField.Placeholder> addedPlaceholders = new ArrayList<>();
        for (int i = 0, j = 0; i < lines.size(); i++) {
            if (i > 0) sb.append(indent);
            while (j < snippet.insert().size() &&
                    snippet.insert().get(j).row() == i) {
                var p = snippet.insert().get(j++);
                addedPlaceholders.add(new CodeField.Placeholder(p.context(), start + sb.length() + p.colum()));
            }
            sb.append(lines.get(i));
            if (i != lines.size() - 1) sb.append('\n');
        }
        backend.insertText(sb.toString());
        if (!addedPlaceholders.isEmpty()) {
            backend.getPlaceholders().addAll(addedPlaceholders);
            backend.getPlaceholders().sort(Comparator.comparing(CodeField.Placeholder::index));
            backend.setCursor(addedPlaceholders.getFirst().index());
            backend.setSelectCursor(backend.getCursor());
        } else {
            CodeField.Placeholder next = backend.getPlaceholders().stream()
                    .filter(p -> p.index() > backend.getCursor())
                    .min(Comparator.comparingInt(CodeField.Placeholder::index))
                    .orElse(null);
            if (next != null) {
                backend.setCursor(next.index());
                backend.setSelectCursor(backend.getCursor());
            }
        }
    }

    @Override
    protected void renderDecorations(@NotNull GuiGraphics guiGraphics) {
        if (scrollbarVisible()) {
            int scrollBarHeight = Mth.clamp(this.height * this.height / (this.getInnerHeight() + 4), 32, this.height);
            int x = this.getX() + this.width;
            int y = Math.max(this.getY(), (int) this.scrollAmount() * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.getY());
            // I know it's weird to use entry sprite for scrollbar, but it looks not too bad, and I'm lazy to make a new one...
            guiGraphics.blitSprite(ENTRY_SPRITES.get(false, false), x-4, y, 4, scrollBarHeight);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE,
                Component.translatable("gui.narrate.editBox", getMessage(), "snippet"));
    }

    public int getInnerHeight() {
        return backend.getCurSnippets().stream()
                .mapToInt(set -> set.fold() ? 0 : set.snippets().size()).sum() * ENTRY_HEIGHT +
                backend.getCurSnippets().size() * DIR_HEIGHT;
    }

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return ENTRY_HEIGHT;
    }
}
