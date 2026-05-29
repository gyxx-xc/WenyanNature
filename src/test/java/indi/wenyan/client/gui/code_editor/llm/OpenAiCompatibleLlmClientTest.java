package indi.wenyan.client.gui.code_editor.llm;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiCompatibleLlmClientTest {

    @Test
    void testTruncateErrorBody() {
        String body = "x".repeat(1200);

        String truncated = OpenAiCompatibleLlmClient.truncateErrorBody(body);

        assertThat(truncated).hasSize(1003);
        assertThat(truncated).endsWith("...");
    }

    @Test
    void testShortErrorBodyIsUnchanged() {
        assertThat(OpenAiCompatibleLlmClient.truncateErrorBody("short")).isEqualTo("short");
    }
}
