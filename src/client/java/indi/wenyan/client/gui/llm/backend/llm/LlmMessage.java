package indi.wenyan.client.gui.llm.backend.llm;

import org.jetbrains.annotations.NotNull;

public record LlmMessage(@NotNull String role, @NotNull String content) {
}
