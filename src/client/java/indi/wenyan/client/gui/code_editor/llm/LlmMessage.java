package indi.wenyan.client.gui.code_editor.llm;

import org.jetbrains.annotations.NotNull;

public record LlmMessage(@NotNull String role, @NotNull String content) {
}
