package indi.wenyan.client.gui.code_editor.llm;

import lombok.Getter;

@Getter
public enum LlmProvider {
    DEEPSEEK("DeepSeek", "https://api.deepseek.com/chat/completions", "deepseek-chat"),
    KIMI("Kimi", "https://api.moonshot.cn/v1/chat/completions", "moonshot-v1-8k"),
    CHATGPT("ChatGPT", "https://api.openai.com/v1/chat/completions", "gpt-4o-mini"),
    QWEN("Qwen", "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", "qwen-plus"),
    GEMINI("Gemini", "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", "gemini-2.5-flash"),
    GROK("Grok", "https://api.x.ai/v1/chat/completions", "grok-2");

    private final String displayName;
    private final String defaultUrl;
    private final String defaultModel;

    LlmProvider(String displayName, String defaultUrl, String defaultModel) {
        this.displayName = displayName;
        this.defaultUrl = defaultUrl;
        this.defaultModel = defaultModel;
    }

    public String getApiKeyEnvVar() {
        return this.name() + "_API_KEY";
    }

    public String getUrlEnvVar() {
        return this.name() + "_URL";
    }

    public String getModelEnvVar() {
        return this.name() + "_MODEL";
    }

    public LlmProvider next() {
        LlmProvider[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
