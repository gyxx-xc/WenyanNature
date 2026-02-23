package indi.wenyan.client.gui.code_editor.widget;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.client.gui.Utils;
import indi.wenyan.client.gui.code_editor.backend.CodeEditorBackend;
import indi.wenyan.client.gui.code_editor.backend.CodeField;
import indi.wenyan.client.gui.code_editor.backend.SnippetSet;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// although it is not a text area,
// for some historical reasons, only text area work for this situation
public class PackageSnippetWidget extends AbstractTextAreaWidget {
    private final Font font;
    private final CodeEditorBackend backend;

    public static final WidgetSprites ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "entry"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_highlighted")); // for scroll bar

    public static final WidgetSprites DIR_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_folded"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_highlighted"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_folded_highlighted"));

    public static final WidgetSprites PACKAGE_DIR_ICON_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_icon"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_icon_highlighted"));
    public static final WidgetSprites CLASS_ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "class_entry"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "class_entry_highlighted"));
    public static final WidgetSprites FIELD_ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "field_entry"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "field_entry_highlighted"));
    public static final WidgetSprites METHOD_ENTRY_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "method_entry"),
            Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "method_entry_highlighted"));

    private static final Utils.BoxInformation buttonPadding =
            new Utils.BoxInformation(3, 3, 3, 3);
    private static final Utils.BoxInformation entryPadding =
            new Utils.BoxInformation(3, 3 + 9, 3, 3);
    public static final int ENTRY_HEIGHT = 9 + buttonPadding.vertical();
    public static final int DIR_HEIGHT = 16 + buttonPadding.vertical();
    public static final int ICON_WIDTH = 16;

    @Setter
    private Runnable resetFocus = null;

    public Optional<SnippetSet.Snippet> getRenderingSnippetTooltip() {
        return Optional.empty();
    }

    public PackageSnippetWidget(Font font, CodeEditorBackend backend, int x, int y, int width, int height) {
        int scrollbarWidth = 4;
        super(x, y, width - scrollbarWidth, height, Component.empty(), new ScrollbarSettings(
                // I know it's weird to use entry sprite for scrollbar, but it looks not too bad, and I'm lazy to make a new one...
                ENTRY_SPRITES.get(false, false),
                null,
                Identifier.withDefaultNamespace("widget/scroller_background"),
                scrollbarWidth, 32, ENTRY_HEIGHT, true));
        this.font = font;
        this.backend = backend;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int currentY = getY() + innerPadding();
        setScrollAmount(scrollAmount()); // clamp scroll amount
        int offsetMouseY = mouseY + (int) scrollAmount();
        for (var pack : backend.getPackages()) {
            renderDir(guiGraphics, mouseX, offsetMouseY, currentY, pack);
            currentY += DIR_HEIGHT;
            if (pack.fold()) continue;
            for (Member member : pack.members()) {
                renderEntry(guiGraphics, mouseX, offsetMouseY, currentY, member);
                currentY += ENTRY_HEIGHT;
            }
        }
    }

    private void renderEntry(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int currentY,
                             Member member) {
        if (withinContentAreaTopBottom(currentY, currentY + ENTRY_HEIGHT)) {
            boolean buttonHovered = mouseX >= getX() + innerPadding() &&
                    mouseX < getX() + getWidth() - innerPadding() &&
                    mouseY >= currentY && mouseY < currentY + ENTRY_HEIGHT;
            final WidgetSprites widgetSprites = switch (member.type()) {
                case CLASS -> CLASS_ENTRY_SPRITES;
                case FIELD -> FIELD_ENTRY_SPRITES;
                case METHOD -> METHOD_ENTRY_SPRITES;
            };
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    widgetSprites.get(true, buttonHovered),
                    getX() + innerPadding(), currentY,
                    this.getWidth() - totalInnerPadding(), ENTRY_HEIGHT
            );

            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(member.name()),
                            width - totalInnerPadding() - entryPadding.horizontal()));
            guiGraphics.drawString(font, text,
                    getX() + innerPadding() + entryPadding.left(), currentY + entryPadding.top(),
                    0xFFFFFFFF, false);
        }
    }

    private void renderDir(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, int currentY,
                           PackageSnippet pack) {
        if (withinContentAreaTopBottom(currentY, currentY + DIR_HEIGHT)) {
            int x = getX() + innerPadding() + buttonPadding.left();
            int y = currentY + buttonPadding.top();

            boolean buttonHoveredY = mouseY >= currentY && mouseY < currentY + DIR_HEIGHT;
            boolean buttonHoveredLeft = mouseX >= getX() + innerPadding() &&
                    mouseX < getX() + innerPadding() + ICON_WIDTH + buttonPadding.horizontal() &&
                    buttonHoveredY;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    PACKAGE_DIR_ICON_SPRITES.get(true, buttonHoveredLeft),
                    getX() + innerPadding(), currentY,
                    ICON_WIDTH + buttonPadding.horizontal(), DIR_HEIGHT);
            guiGraphics.renderFakeItem(pack.itemStack, x, y);

            boolean buttonHoveredRight = mouseX >= getX() + innerPadding() + ICON_WIDTH + buttonPadding.horizontal() &&
                    mouseX < getX() + getWidth() - innerPadding() &&
                    buttonHoveredY;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    DIR_SPRITES.get(!pack.fold(), buttonHoveredRight),
                    getX() + innerPadding() + ICON_WIDTH + buttonPadding.horizontal(), currentY,
                    this.getWidth() - totalInnerPadding() - ICON_WIDTH - buttonPadding.horizontal(), DIR_HEIGHT
            );
            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(pack.name()),
                            width - totalInnerPadding() - buttonPadding.horizontal()));
            guiGraphics.drawString(font, text, x + ICON_WIDTH + buttonPadding.horizontal() + buttonPadding.right(), y,
                    0xFFFFFFFF, false);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        var result = clickEntry(mouseX, mouseY, button);
        return super.mouseClicked(event, doubleClick) || result;
    }

    private boolean clickEntry(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            boolean buttonHoveredX = mouseX >= getX() + innerPadding() &&
                    mouseX < getX() + getWidth() - innerPadding();
            if (!buttonHoveredX)
                return false;
            double y = mouseY - getY() - innerPadding() + scrollAmount();
            double currentY = 0;
            for (var pack : backend.getPackages()) {
                if (y >= currentY && y < currentY + DIR_HEIGHT) {
                    double x = mouseX - getX() - innerPadding();
                    if (0 <= x && x < ICON_WIDTH + buttonPadding.horizontal())
                        insertId(pack.name());
                    else
                        pack.fold(!pack.fold());
                    return true;
                }
                currentY += DIR_HEIGHT;
                if (pack.fold()) continue;
                for (Member member : pack.members()) {
                    if (y >= currentY && y < currentY + ENTRY_HEIGHT) {
                        // clicked on snippet
                        insertId(member.name());
                        return true;
                    }
                    currentY += ENTRY_HEIGHT;
                }
            }
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE,
                Component.translatable("gui.narrate.editBox", getMessage(), "snippet"));
    }

    public int getInnerHeight() {
        return backend.getPackages().stream()
                .mapToInt(pack -> pack.fold() ? 0 : pack.members.size()).sum() * ENTRY_HEIGHT +
                backend.getPackages().size() * DIR_HEIGHT;
    }

    private void insertId(String id) {
        if (resetFocus != null) resetFocus.run();
        backend.insertText(id);
        CodeField.Placeholder next = backend.getPlaceholders().stream()
                .filter(p -> p.index() > backend.getCursor())
                .min(Comparator.comparingInt(CodeField.Placeholder::index))
                .orElse(null);
        if (next != null) {
            backend.setCursor(next.index());
            backend.setSelectCursor(backend.getCursor());
        }
    }

    @Data
    @Accessors(fluent = true)
    public static class PackageSnippet {
        final ItemStack itemStack;
        final String name;
        final List<Member> members;
        boolean fold = false;

        public PackageSnippet(ItemStack itemStack, String name, List<Member> members) {
            this.itemStack = itemStack;
            this.name = name;
            this.members = members;
        }
    }

    public record Member(String name, MemberType type) {
    }

    public enum MemberType {
        METHOD,
        FIELD,
        CLASS
    }
}
