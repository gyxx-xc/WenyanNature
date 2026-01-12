package indi.wenyan.content.gui.code_editor;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.content.gui.Utils;
import lombok.Data;
import lombok.experimental.Accessors;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PackageSnippetWidget extends AbstractScrollWidget {
    private final Font font;
    private final CodeEditorBackend backend;

    public static final WidgetSprites ENTRY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "entry_highlighted")); // for scroll bar

    public static final WidgetSprites DIR_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_folded"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_highlighted"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_entry_folded_highlighted"));

    public static final WidgetSprites PACKAGE_DIR_ICON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_icon"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "package_dir_icon_highlighted"));
    public static final WidgetSprites CLASS_ENTRY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "class_entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "class_entry_highlighted"));
    public static final WidgetSprites FIELD_ENTRY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "field_entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "field_entry_highlighted"));
    public static final WidgetSprites METHOD_ENTRY_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "method_entry"),
            ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "method_entry_highlighted"));

    private static final Utils.BoxInformation buttonPadding =
            new Utils.BoxInformation(3, 3, 3, 3);
    private static final Utils.BoxInformation entryPadding =
            new Utils.BoxInformation(3, 3 + 9, 3, 3);
    public static final int ENTRY_HEIGHT = 9 + buttonPadding.vertical();
    public static final int DIR_HEIGHT = 16 + buttonPadding.vertical();
    public static final int ICON_WIDTH = 16;

    public Optional<SnippetSet.Snippet> getRenderingSnippetTooltip() {
        return Optional.empty();
    }

    public PackageSnippetWidget(Font font, int x, int y, int width, int height, CodeEditorBackend backend) {
        super(x, y, width, height, Component.empty());
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
            guiGraphics.blitSprite(
                    widgetSprites.get(true, buttonHovered),
                    getX() + innerPadding(), currentY,
                    this.getWidth() - totalInnerPadding(), ENTRY_HEIGHT
            );

            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(member.name()),
                            width - totalInnerPadding() - entryPadding.horizontal()));
            guiGraphics.drawString(font, text,
                    getX() + innerPadding() + entryPadding.left(), currentY + entryPadding.top(),
                    0xFFFFFF, false);
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
            guiGraphics.blitSprite(
                    PACKAGE_DIR_ICON_SPRITES.get(true, buttonHoveredLeft),
                    getX() + innerPadding(), currentY,
                    ICON_WIDTH + buttonPadding.horizontal(), DIR_HEIGHT);
            guiGraphics.renderFakeItem(pack.itemStack, x, y);

            boolean buttonHoveredRight = mouseX >= getX() + innerPadding() + ICON_WIDTH + buttonPadding.horizontal() &&
                    mouseX < getX() + getWidth() - innerPadding() &&
                    buttonHoveredY;
            guiGraphics.blitSprite(
                    DIR_SPRITES.get(!pack.fold(), buttonHoveredRight),
                    getX() + innerPadding() + ICON_WIDTH + buttonPadding.horizontal(), currentY,
                    this.getWidth() - totalInnerPadding() - ICON_WIDTH - buttonPadding.horizontal(), DIR_HEIGHT
            );
            var text = Language.getInstance().getVisualOrder(
                    font.ellipsize(FormattedText.of(pack.name()),
                            width - totalInnerPadding() - buttonPadding.horizontal()));
            guiGraphics.drawString(font, text, x + ICON_WIDTH + buttonPadding.right(), y,
                    0xFFFFFF, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            double y = mouseY - getY() - innerPadding() + scrollAmount();
            double currentY = 0;
            for (var pack : backend.getPackages()) {
                if (y >= currentY && y < currentY + DIR_HEIGHT) {
                    double x = mouseX - getX() - innerPadding();
                    if (0 <= x && x < ICON_WIDTH + buttonPadding.horizontal())
                        backend.insertSnippet(new SnippetSet.Snippet(pack.name(), List.of(pack.name()), List.of()));
                    else
                        pack.fold(!pack.fold());
                    return true;
                }
                currentY += DIR_HEIGHT;
                if (pack.fold()) continue;
                for (Member member : pack.members()) {
                    if (y >= currentY && y < currentY + ENTRY_HEIGHT) {
                        // clicked on snippet
                        backend.insertSnippet(new SnippetSet.Snippet(member.name(), List.of(member.name()), List.of()));
                        return true;
                    }
                    currentY += ENTRY_HEIGHT;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderDecorations(@NotNull GuiGraphics guiGraphics) {
        if (scrollbarVisible()) {
            int scrollBarHeight = Mth.clamp(this.height * this.height / (this.getInnerHeight() + 4), 32, this.height);
            int x = this.getX() + this.width;
            int y = Math.max(this.getY(), (int) this.scrollAmount() * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.getY());
            // I know it's weird to use entry sprite for scrollbar, but it looks not too bad, and I'm lazy to make a new one...
            guiGraphics.blitSprite(ENTRY_SPRITES.get(false, false), x - 4, y, 4, scrollBarHeight);
        }
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

    protected boolean scrollbarVisible() {
        return getInnerHeight() > getHeight() - totalInnerPadding();
    }

    protected double scrollRate() {
        return ENTRY_HEIGHT;
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
