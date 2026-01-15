package indi.wenyan.content.gui.code_editor;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CodeEditorScreen extends Screen {

    private final CodeEditorBackend backend;

    public static final int CHARACTER_LIMIT = 16384;
    public static final int TITLE_LENGTH_LIMIT = 18;

    @Getter
    private CodeEditorWidget textFieldWidget;
    private SnippetWidget snippetWidget;
    private PackageSnippetWidget packageWidget;
    private EditBox titleBar;
    private FittingMultiLineTextWidget outputWindow;

    public CodeEditorScreen(CodeEditorBackend backend) {
        super(Component.empty());
        this.backend = backend;
    }

    @Override
    protected void init() {
        int textFieldWidth = Mth.clamp(width / 2, 50, CodeEditorWidget.WIDTH);
        textFieldWidget = new CodeEditorWidget(font, backend,
                (width - textFieldWidth) / 2, 15,
                textFieldWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        addRenderableWidget(textFieldWidget);

        // -4 is spacing
        int snippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 140);
        snippetWidget = new SnippetWidget(font, backend,
                0, 15,
                snippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        addRenderableWidget(snippetWidget);

        int packageSnippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 280);
        packageWidget = new PackageSnippetWidget(font, backend,
                width - packageSnippetWidth, 15,
                packageSnippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        addRenderableWidget(packageWidget);

        titleBar = new FuzhouNameWidget(font, snippetWidth + 4, 2,
                width - (snippetWidth + 4) - (packageSnippetWidth + 4), 15,
                Component.translatable("gui.wenyan.snippet_name"));
        titleBar.setTextColor(-1);
        titleBar.setBordered(false);
        titleBar.setMaxLength(18);
        titleBar.setValue(backend.getTitle());
        titleBar.setResponder(backend::setTitle);
        addRenderableWidget(titleBar);

        outputWindow = new FittingMultiLineTextWidget(
                0, 15 + Math.min(height - 30, CodeEditorWidget.HEIGH),
                width, 30,
                Component.literal(("abcd".repeat(80)+"\n").repeat(10)), font);
        addRenderableWidget(outputWindow);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // tooltips
        snippetWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
        packageWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
    }

    public void renderSnippetTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY,
            SnippetSet.Snippet snippet) {
        List<Component> tooltip = Lists.newArrayList();
        tooltip.add(Component.literal(snippet.title()));
        if (!hasShiftDown()) {
            tooltip.add(Component.translatable("gui.wenyan.hold_shift").withStyle(ChatFormatting.GRAY));
        } else {
            int curInsert = 0;
            for (int row = 0; row < snippet.lines().size(); row++) {
                String line = snippet.lines().get(row);
                int curColum = 0;
                MutableComponent lineComp = Component.literal("");
                while (curInsert < snippet.insert().size() &&
                        snippet.insert().get(curInsert).row() == row) {
                    var placeholder = snippet.insert().get(curInsert++);

                    Component textComp = Component.literal(line.substring(curColum,
                            placeholder.colum())).withStyle(ChatFormatting.GRAY);

                    Component placeholderComp = Component.literal(placeholder.context().getValue())
                            .withStyle(Style.EMPTY.withColor(placeholder.context().getColor()));
                    lineComp.append(textComp).append(placeholderComp);
                    curColum = placeholder.colum();
                }
                Component textComp = Component.literal(line.substring(curColum))
                        .withStyle(ChatFormatting.GRAY);
                lineComp.append(textComp);
                tooltip.add(lineComp);
            }
        }
        guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(guiGraphics);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        if (listener == snippetWidget || listener == packageWidget || listener == outputWindow)
            super.setFocused(textFieldWidget);
        else
            super.setFocused(listener);
    }

    @Override
    public void onClose() {
        backend.save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
