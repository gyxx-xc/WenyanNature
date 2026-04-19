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
import java.util.Optional;

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

    // ── AI bar ──
    private EditBox aiPromptBox;
    private Button aiGenerateButton;
    /** True while a DeepSeek request is in-flight. */
    private boolean aiGenerating = false;
    /** Shown in output area while generating / on error. */
    private String aiStatusMessage = null;

    public RunnerBlockScreen(RunnerBlockBackend backend) {
        super(Component.empty());
        this.backend = backend;
    }

    @Override
    protected void init() {
        // Ensure the .env template exists so players know where to put the key
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

        // ── AI bar layout ──────────────────────────────────────────────────
        // The AI bar sits below the output window; we shrink outputWindowHeight
        // to make room for it.
        int aiBarHeight = font.lineHeight + 8;  // 4 px padding top + bottom
        int outputAreaY = textFileHeight + titleBarHeight + 4;
        int outputWindowHeight = height - outputAreaY - aiBarHeight - 2;

        outputWindow = new CodeOutputWidget(
                snippetWidth + 4, outputAreaY,
                textFieldWidth, Math.max(outputWindowHeight, 0),
                Component.literal(""), font, backend);
        addRenderableWidget(outputWindow);

        // ── AI input bar ───────────────────────────────────────────────────
        int aiBarY = outputAreaY + Math.max(outputWindowHeight, 0) + 2;
        int aiBarX = snippetWidth + 4;

        int buttonWidth = 50;
        int promptBoxWidth = textFieldWidth - buttonWidth - 2;
        int promptBoxHeight = aiBarHeight;

        // Prompt EditBox (styled like FuzhouNameWidget: black bg, white text, no border)
        aiPromptBox = new EditBox(font,
                aiBarX, aiBarY,
                promptBoxWidth, promptBoxHeight,
                Component.translatable(GuiText.AiPromptLabel.getTranslationKey()));
        aiPromptBox.setBordered(true);
        aiPromptBox.setTextColor(0xFFFFFFFF);
        aiPromptBox.setMaxLength(512);
        aiPromptBox.setHint(Component.translatable(GuiText.AiPromptLabel.getTranslationKey())
                .withStyle(Style.EMPTY.withColor(0xFF888888)));
        addRenderableWidget(aiPromptBox);

        // Generate button
        aiGenerateButton = Button.builder(
                        Component.translatable(GuiText.AiGenerateButton.getTranslationKey()),
                        btn -> onAiGenerate())
                .bounds(aiBarX + promptBoxWidth + 2, aiBarY, buttonWidth, promptBoxHeight)
                .build();
        addRenderableWidget(aiGenerateButton);
    }

    // ── AI generation logic ────────────────────────────────────────────────

    private void onAiGenerate() {
        if (aiGenerating) return;

        String prompt = aiPromptBox.getValue().strip();
        if (prompt.isEmpty()) return;

        Optional<String> apiKeyOpt = AiConfig.loadApiKey();
        if (apiKeyOpt.isEmpty()) {
            aiStatusMessage = "[WenyanNature AI] "
                    + AiConfig.getEnvPath()
                    + " not found or DEEPSEEK_API_KEY is empty";
            return;
        }

        aiGenerating = true;
        aiGenerateButton.active = false;
        aiStatusMessage = GuiText.AiGenerating.string();

        String apiKey = apiKeyOpt.get();
        DeepSeekClient.generate(
                apiKey,
                prompt,
                generatedCode -> {
                    // Overwrite editor: select all then insert
                    int len = backend.getContent().length();
                    backend.setSelectCursor(0);
                    backend.setCursor(len);
                    backend.insertText(generatedCode);
                    aiStatusMessage = null;
                    aiGenerating = false;
                    aiGenerateButton.active = true;
                },
                errorMsg -> {
                    aiStatusMessage = GuiText.AiError.string() + ": " + errorMsg;
                    aiGenerating = false;
                    aiGenerateButton.active = true;
                }
        );
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics,
                       int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

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
