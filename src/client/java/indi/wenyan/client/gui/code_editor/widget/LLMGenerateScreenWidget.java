package indi.wenyan.client.gui.code_editor.widget;

import indi.wenyan.client.gui.code_editor.backend.RunnerBlockBackend;
import indi.wenyan.client.gui.code_editor.llm.LlmCodeDiff;
import indi.wenyan.client.gui.code_editor.llm.LlmSession;
import indi.wenyan.setup.language.GuiText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Embedded AI code-generation panel that occupies the same space as
 * {@link indi.wenyan.client.gui.code_editor.widget.CodeOutputWidget}.
 *
 * <p>Not a real Minecraft {@code Screen}; it registers its child widgets into
 * the owning {@link net.minecraft.client.gui.screens.Screen} via a consumer
 * and handles visibility as a single logical unit.
 *
 * <p>The panel shows:
 * <ul>
 *   <li>A single-line black background</li>
 *   <li>A prefix prompt text</li>
 *   <li>A prompt {@link EditBox}</li>
 *   <li>A suffix string</li>
 *   <li>A "✨制符" {@link Button} attached to the right</li>
 * </ul>
 */
public class LLMGenerateScreenWidget {

    private static final int STATUS_COLOR     = 0xFFFFD700; // gold
    private static final int FOREGROUND_COLOR = 0xFFFFFFFF;

    private static final int INPUT_H = 15;
    private static final int ROW_GAP = 4;
    private static final ExecutorService DIFF_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "WenyanNature-LLM-Diff");
        thread.setDaemon(true);
        return thread;
    });

    // ── Layout (set by init) ──
    private int panelX, panelY, panelW;
    private Font font;

    @Getter
    private final LlmSession session = new LlmSession();

    // ── Child widgets ──
    private EditBox promptBox;
    private Button  generateButton;
    private Button  newMemoryButton;
    private Button  fixButton;
    private Button  switchModelButton;
    private Button  applyButton;
    private Button  cancelButton;

    // ── State (survives screen resize because this object is reused) ──
    private boolean generating    = false;
    private String  statusMessage = null;
    private boolean visible       = false;
    private String  pendingCode   = null;
    private String  pendingBaseCode = null;
    private List<LlmCodeDiff.Line> pendingDiff = List.of();
    private CodeEditorWidget codeEditorWidget;
    private int candidateRevision = 0;

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    /**
     * Called by the owning screen's {@code init()} on every resize.
     * Creates fresh widgets at the new coordinates and registers them via
     * {@code addWidget}. The previous {@link #visible} and
     * {@link #statusMessage} state are preserved.
     *
     * @param font      Font renderer
     * @param backend   The full runner backend (for code overwriting and console output access)
     * @param x         Left edge of the panel (same as CodeOutputWidget)
     * @param y         Top edge
     * @param width     Panel width
     * @param height    Panel height
     * @param addWidget Consumer that registers a widget into the owning screen
     */
    public void init(Font font, RunnerBlockBackend backend,
                     CodeEditorWidget codeEditorWidget,
                     int x, int y, int width, int height,
                     int btnX, int btnY, int btnW, int btnH,
                     Consumer<AbstractWidget> addWidget) {
        this.font   = font;
        this.panelX = x;
        this.panelY = y;
        this.panelW = width;
        this.codeEditorWidget = codeEditorWidget;

        if (btnW > 0) {
            switchModelButton = Button.builder(
                            Component.literal("模型: " + session.getProvider().getDisplayName()),
                            btn -> {
                                session.setProvider(session.getProvider().next());
                                btn.setMessage(Component.literal("模型: " + session.getProvider().getDisplayName()));
                            })
                    .bounds(btnX, btnY, btnW, btnH)
                    .build();
            addWidget.accept(switchModelButton);
        }

        int buttonW = 60;
        int availableW = width - buttonW - 4;

        int promptLength = font.width(GuiText.AiPromptLabel.text());
        int editBoxX = x + promptLength + 4;
        int editBoxW = availableW - promptLength - 8;

        // ── Prompt EditBox ──
        promptBox = new EditBox(font, editBoxX, y, editBoxW, INPUT_H, Component.empty());
        // Vanilla border is kept
        promptBox.setMaxLength(512);
        addWidget.accept(promptBox);

        // ── Generate button ──
        generateButton = Button.builder(
                        Component.translatable(GuiText.AiGenerateButton.getTranslationKey()),
                        btn -> triggerGenerate(backend, false))
                .bounds(x + availableW + 4, y, buttonW, INPUT_H)
                .build();
        addWidget.accept(generateButton);

        // ── Additional Action Buttons ──
        int actionBtnY = y + INPUT_H + ROW_GAP;
        int actionBtnW = editBoxW / 2 - 2;

        newMemoryButton = Button.builder(
                        Component.literal("新记忆"),
                        btn -> {
                            session.clearHistory();
                            generating = false;
                            clearPendingCode();
                            statusMessage = null;
                            updateButtonState();
                        })
                .bounds(editBoxX, actionBtnY, actionBtnW, INPUT_H)
                .build();
        addWidget.accept(newMemoryButton);

        fixButton = Button.builder(
                        Component.literal("修复").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(0xFFB347)),
                        btn -> triggerGenerate(backend, true))
                .bounds(editBoxX + actionBtnW + 4, actionBtnY, actionBtnW, INPUT_H)
                .build();
        addWidget.accept(fixButton);

        int candidateBtnY = actionBtnY + INPUT_H + ROW_GAP;
        int candidateBtnW = editBoxW / 2 - 2;

        applyButton = Button.builder(
                        Component.literal("应用"),
                        btn -> applyPendingCode(backend))
                .bounds(editBoxX, candidateBtnY, candidateBtnW, INPUT_H)
                .build();
        addWidget.accept(applyButton);

        cancelButton = Button.builder(
                        Component.literal("取消"),
                        btn -> {
                            clearPendingCode();
                            statusMessage = null;
                            updateButtonState();
                        })
                .bounds(editBoxX + candidateBtnW + 4, candidateBtnY, candidateBtnW, INPUT_H)
                .build();
        addWidget.accept(cancelButton);

        // Restore visibility for post-resize state
        applyVisibility();
        updateButtonState();
    }

    // -----------------------------------------------------------------------
    // AI logic
    // -----------------------------------------------------------------------

    private void triggerGenerate(RunnerBlockBackend backend, boolean isFix) {
        if (generating) return;

        generating = true;
        clearPendingCode();
        pendingBaseCode = backend.getContent();
        int generationRevision = candidateRevision;
        statusMessage = GuiText.AiGenerating.string();
        updateButtonState();

        Consumer<String> onSuccess = code -> {
            String baseCode = pendingBaseCode;
            CompletableFuture
                    .supplyAsync(() -> baseCode == null ? List.<LlmCodeDiff.Line>of() : LlmCodeDiff.diff(baseCode, code),
                            DIFF_EXECUTOR)
                    .thenAccept(diff -> net.minecraft.client.Minecraft.getInstance().execute(() -> {
                        if (generationRevision != candidateRevision) {
                            return;
                        }
                        pendingCode = code;
                        pendingDiff = diff;
                        statusMessage = "大儒已成稿，确认后应用";
                        generating = false;
                        showPendingDiff();
                        updateButtonState();
                    }));
        };

        Consumer<String> onError = err -> {
            statusMessage = GuiText.AiError.string() + ": " + err;
            generating = false;
            updateButtonState();
        };

        if (isFix) {
            StringBuilder sb = new StringBuilder();
            for (Component c : backend.getOutput()) {
                sb.append(c.getString()).append("\n");
            }
            session.generateFix(backend.getContent(), sb.toString(), onSuccess, onError);
        } else {
            if (promptBox == null || promptBox.getValue().strip().isEmpty()) {
                // Do not proceed with an empty prompt box on standard generate
                generating = false;
                statusMessage = null;
                updateButtonState();
                return;
            }
            String prompt = promptBox.getValue().strip();
            session.generateCode(prompt, backend.getContent(), onSuccess, onError);
        }
    }

    private void applyPendingCode(RunnerBlockBackend backend) {
        if (pendingCode == null)
            return;
        if (!backend.getContent().equals(pendingBaseCode)) {
            statusMessage = "当前代码已变化，请重新生成或取消候选";
            clearPreview();
            updateButtonState();
            return;
        }
        backend.setSelectCursor(0);
        backend.setCursor(backend.getContent().length());
        backend.insertText(pendingCode);
        clearPendingCode();
        statusMessage = "已应用";
        updateButtonState();
    }

    private void clearPendingCode() {
        candidateRevision++;
        clearPreview();
        pendingCode = null;
        pendingBaseCode = null;
        pendingDiff = List.of();
    }

    private void clearPreview() {
        if (codeEditorWidget != null && codeEditorWidget.isLlmPreviewing()) {
            codeEditorWidget.clearLlmPreview();
        }
    }

    private void showPendingDiff() {
        if (codeEditorWidget != null && hasPendingCode()) {
            codeEditorWidget.showLlmPreview(pendingCode, pendingDiff);
        }
    }

    // -----------------------------------------------------------------------
    // Visibility
    // -----------------------------------------------------------------------

    /**
     * Shows or hides this panel and all its child widgets.
     * Hiding also clears any displayed status message.
     */
    public void setVisible(boolean v) {
        this.visible = v;
        if (!v) {
            statusMessage = null;
            clearPreview();
        }
        applyVisibility();
    }

    /** @return whether this panel is currently visible */
    public boolean isVisible() {
        return visible;
    }

    private void applyVisibility() {
        if (promptBox       != null) promptBox.visible       = visible;
        if (generateButton != null)   generateButton.visible = visible;
        if (newMemoryButton != null)  newMemoryButton.visible = visible;
        if (switchModelButton != null) switchModelButton.visible = visible;
        if (fixButton       != null) fixButton.visible       = visible;
        if (applyButton     != null) applyButton.visible     = visible;
        if (cancelButton    != null) cancelButton.visible    = visible;
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasPendingCode = hasPendingCode();
        if (generateButton != null) generateButton.active = visible && !generating;
        if (fixButton != null) fixButton.active = visible && !generating;
        if (newMemoryButton != null) newMemoryButton.active = visible;
        if (switchModelButton != null) switchModelButton.active = visible && !generating;
        if (applyButton != null) applyButton.active = visible && !generating && hasPendingCode;
        if (cancelButton != null) cancelButton.active = visible && !generating && hasPendingCode;
    }

    private boolean hasPendingCode() {
        return pendingCode != null && !pendingCode.isBlank();
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    /**
     * Renders the panel's background and status text.
     * The child widgets render themselves automatically via the screen's
     * renderable list.
     * <p>Call this from the owning screen's {@code extractRenderState}.
     */
    public void render(GuiGraphicsExtractor g) {
        if (!visible || font == null) return;

        // Render prefix (e.g. "指示大儒之事：")
        Component prefixText = GuiText.AiPromptLabel.text();
        g.text(font, prefixText, panelX + 4, panelY + 4, FOREGROUND_COLOR, false);

        // The promptBox (EditBox) itself renders automatically via widget list

        // Status message (generating / error) rendered slightly below the box
        if (statusMessage != null) {
            g.text(font, statusMessage, panelX + 4, panelY + INPUT_H + 8, STATUS_COLOR, false);
        }

    }
}
