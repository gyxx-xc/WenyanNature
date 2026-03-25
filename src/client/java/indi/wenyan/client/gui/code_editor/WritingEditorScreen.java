package indi.wenyan.client.gui.code_editor;

import indi.wenyan.client.gui.code_editor.backend.WritingBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.behaviour.SnippetSet;
import indi.wenyan.client.gui.code_editor.widget.CodeEditorWidget;
import indi.wenyan.client.gui.code_editor.widget.FuzhouNameWidget;
import indi.wenyan.client.gui.code_editor.widget.SnippetWidget;
import indi.wenyan.setup.language.GuiText;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WritingEditorScreen extends Screen {

    private final WritingBlockBackend backend;

    @Getter
    private CodeEditorWidget textFieldWidget;
    private SnippetWidget snippetWidget;
//    private PackageSnippetWidget packageWidget
    @SuppressWarnings("FieldCanBeLocal")
    private EditBox titleBar;

    public WritingEditorScreen(WritingBlockBackend backend) {
        super(Component.empty());
        this.backend = backend;
    }

    @Override
    protected void init() {
        int titleBarHeight = 15;
        int textFieldWidth = Mth.clamp(width / 2, 50, CodeEditorWidget.WIDTH);
        int textFileHeight = Math.min(height - 30, CodeEditorWidget.HEIGH);
        textFieldWidget = new CodeEditorWidget(font, backend,
                (width - textFieldWidth) / 2, titleBarHeight,
                textFieldWidth, textFileHeight);
        addRenderableWidget(textFieldWidget);

        // -4 is spacing
        int snippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 140);
        snippetWidget = new SnippetWidget(font, backend,
                0, 15,
                snippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        snippetWidget.setResetFocus(() -> setFocused(textFieldWidget));
        addRenderableWidget(snippetWidget);

//        int packageSnippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 280);
//        packageWidget = new PackageSnippetWidget(font, backend,
//                width - packageSnippetWidth, 15,
//                packageSnippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
//        packageWidget.setResetFocus(() -> setFocused(textFieldWidget));
//        addRenderableWidget(packageWidget);

        titleBar = new FuzhouNameWidget(font, snippetWidth + 4, 2,
                textFieldWidth, titleBarHeight,
                Component.literal(""), backend);
        titleBar.setTextColor(-1);
        titleBar.setBordered(false);
        titleBar.setMaxLength(18);
        addRenderableWidget(titleBar);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics,
                       int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // tooltips
        snippetWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
//        packageWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
    }

    // STUB/HACK
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.hasShiftDown()) hasShiftDown = true;
        return super.keyPressed(event);
    }
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (event.hasShiftDown()) hasShiftDown = false;
        return super.keyReleased(event);
    }
    private boolean hasShiftDown = false;
    public void renderSnippetTooltip(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
                                     SnippetSet.Snippet snippet) {
        List<ClientTooltipComponent> tooltip = Lists.newArrayList();
        tooltip.add(ClientTooltipComponent.create(FormattedCharSequence.forward(snippet.title(), Style.EMPTY)));
        if (!hasShiftDown) {
            tooltip.add(ClientTooltipComponent.create(FormattedCharSequence.forward(
                    GuiText.HoldShift.string(), Style.EMPTY.withColor(ChatFormatting.GRAY))));
        } else {
            int curInsert = 0;
            for (int row = 0; row < snippet.lines().size(); row++) {
                String line = snippet.lines().get(row);
                int curColum = 0;
                List<FormattedCharSequence> lineComp = new ArrayList<>();
                while (curInsert < snippet.insert().size() &&
                        snippet.insert().get(curInsert).row() == row) {
                    var placeholder = snippet.insert().get(curInsert++);

                    var textComp = FormattedCharSequence.forward(line.substring(curColum, placeholder.colum()),
                            Style.EMPTY.withColor(ChatFormatting.GRAY));
                    var placeholderComp = FormattedCharSequence.forward(placeholder.context().getValue(),
                            Style.EMPTY.withColor(placeholder.context().getColor()));
                    lineComp.add(textComp);
                    lineComp.add(placeholderComp);
                    curColum = placeholder.colum();
                }
                var textComp = FormattedCharSequence.forward(line.substring(curColum),
                        Style.EMPTY.withColor(ChatFormatting.GRAY));
                lineComp.add(textComp);
                tooltip.add(ClientTooltipComponent.create(FormattedCharSequence.composite(lineComp)));
            }
        }
        guiGraphics.tooltip(font, tooltip, mouseX, mouseY,
                DefaultTooltipPositioner.INSTANCE,
                ItemStack.EMPTY.get(DataComponents.TOOLTIP_STYLE));
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        extractTransparentBackground(guiGraphics);
    }

    @Override
    public void tick() {
        super.tick();
        backend.tick();
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
