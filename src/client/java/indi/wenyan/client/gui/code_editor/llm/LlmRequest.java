package indi.wenyan.client.gui.code_editor.llm;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LlmRequest(
        @NotNull String apiUrl,
        @NotNull String model,
        @NotNull String apiKey,
        @NotNull List<LlmMessage> messages) {
}
