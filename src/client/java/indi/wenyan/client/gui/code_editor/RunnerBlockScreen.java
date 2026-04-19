package indi.wenyan.client.gui.code_editor;

import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.backend.behaviour.SnippetSet;
import indi.wenyan.client.gui.code_editor.widget.*;
import indi.wenyan.setup.language.GuiText;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
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

public class RunnerBlockScreen extends Screen {

    private final RunnerBlockBackend backend;

    public static final int CHARACTER_LIMIT = 16384;
    public static final int TITLE_LENGTH_LIMIT = 18;

    @Getter
    private CodeEditorWidget textFieldWidget;
    private SnippetWidget snippetWidget;
    private PackageSnippetWidget packageWidget;
    @SuppressWarnings("FieldCanBeLocal")
    private EditBox titleBar;
    @SuppressWarnings("FieldCanBeLocal")
    private CodeOutputWidget outputWindow;

    // ── AI panel ──
    /** Toggleable AI code-generation panel; occupies the same area as outputWindow. */
    private final LLMGenerateScreenWidget llmGenerateScreen = new LLMGenerateScreenWidget();
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnOutputPanel;
    @SuppressWarnings("FieldCanBeLocal")
    private Button btnLlmPanel;

    public RunnerBlockScreen(RunnerBlockBackend backend) {
        super(Component.empty());
        this.backend = backend;
    }

    @Override
    protected void init() {
        AiConfig.createTemplateIfAbsent();

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
                0, titleBarHeight,
                snippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        snippetWidget.setResetFocus(() -> setFocused(textFieldWidget));
        addRenderableWidget(snippetWidget);

        int packageSnippetWidth = Mth.clamp((width - textFieldWidth) / 2 - 4, 0, 280);
        packageWidget = new PackageSnippetWidget(font, backend,
                width - packageSnippetWidth, titleBarHeight,
                packageSnippetWidth, Math.min(height - 30, CodeEditorWidget.HEIGH));
        packageWidget.setResetFocus(() -> setFocused(textFieldWidget));
        addRenderableWidget(packageWidget);

        titleBar = new FuzhouNameWidget(font, snippetWidth + 4, 2,
                width - (snippetWidth + 4) - (packageSnippetWidth + 4), titleBarHeight,
                Component.literal(""), backend);
        titleBar.setTextColor(-1);
        titleBar.setBordered(false);
        titleBar.setMaxLength(18);
        addRenderableWidget(titleBar);

        // ── Output window (restored to full bottom-half height) ──────────────────
        int outputAreaY  = textFileHeight + titleBarHeight + 4;
        int outputHeight = Math.max(height - outputAreaY, 0);
        int outputX      = snippetWidth + 4;

        outputWindow = new CodeOutputWidget(
                outputX, outputAreaY,
                textFieldWidth, outputHeight,
                Component.literal(""), font, backend);
        addRenderableWidget(outputWindow);

        // ── LLM panel (same area, initially hidden) ────────────────────────────
        llmGenerateScreen.init(font, backend,
                outputX, outputAreaY,
                textFieldWidth, outputHeight,
                this::addRenderableWidget);

        // Restore visibility state across screen resizes
        if (llmGenerateScreen.isVisible()) {
            outputWindow.visible = false;
        } else {
            llmGenerateScreen.setVisible(false);
        }

        // ── Mode selection buttons: right column, just below packageSnippetWidget ────────
        if (packageSnippetWidth > 0) {
            int btnY = titleBarHeight + textFileHeight + 4;
            int btnH = font.lineHeight + 6;
            int btnW = (packageSnippetWidth - 4) / 2;
            int startX = width - packageSnippetWidth;

            btnOutputPanel = Button.builder(
                            Component.translatable(GuiText.LlmPanelBack.getTranslationKey()),
                            btn -> setPanelMode(false))
                    .bounds(startX, btnY, btnW, btnH)
                    .build();

            btnLlmPanel = Button.builder(
                            Component.translatable(GuiText.LlmPanelToggle.getTranslationKey()),
                            btn -> setPanelMode(true))
                    .bounds(startX + btnW + 4, btnY, btnW, btnH)
                    .build();

            addRenderableWidget(btnOutputPanel);
            addRenderableWidget(btnLlmPanel);

            // visually update state
            updatePanelButtons();
        }
    }

    // ── Panel Switch Logic ───────────────────────────────────────────

    private void setPanelMode(boolean showLlm) {
        llmGenerateScreen.setVisible(showLlm);
        outputWindow.visible = !showLlm;
        updatePanelButtons();
    }

    private void updatePanelButtons() {
        if (btnLlmPanel != null && btnOutputPanel != null) {
            boolean isLlm = llmGenerateScreen.isVisible();
            btnLlmPanel.active = !isLlm;
            btnOutputPanel.active = isLlm;
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics,
                       int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // LLM panel background + status (rendered before widget layer)
        llmGenerateScreen.render(guiGraphics);

        // tooltips
        snippetWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
        packageWidget.getRenderingSnippetTooltip().ifPresent(s -> renderSnippetTooltip(guiGraphics, mouseX, mouseY, s));
    }

    public void renderSnippetTooltip(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY,
                                     SnippetSet.Snippet snippet) {
        List<ClientTooltipComponent> tooltip = Lists.newArrayList();
        tooltip.add(ClientTooltipComponent.create(FormattedCharSequence.forward(snippet.title(), Style.EMPTY)));
        // same as ClientTooltipFlag
        boolean hasShiftDown = Minecraft.getInstance().hasShiftDown();
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
