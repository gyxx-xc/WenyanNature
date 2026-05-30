package indi.wenyan.client.gui.llm.backend.llm;

import lombok.Getter;

@Getter
public enum LlmProvider {
    DEEPSEEK("DeepSeek", "https://api.deepseek.com/chat/completions", "deepseek-v4-flash", "deepseek-reasoner"),
    KIMI("Kimi", "https://api.moonshot.cn/v1/chat/completions", "moonshot-v1-8k", "kimi-k2-thinking"),
    CHATGPT("ChatGPT", "https://api.openai.com/v1/chat/completions", "gpt-5.4-mini", "gpt-5.4"),
    QWEN("Qwen", "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions", "qwen3.6-plus", "qwen3.6-max"),
    GEMINI("Gemini", "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", "gemini-3.5-flash", "gemini-3.5-pro"),
    GROK("Grok", "https://api.x.ai/v1/chat/completions", "grok-4.3", "grok-4.3-thinking"),
    CUSTOM("Custom", "", "", "");

    private final String displayName;
    private final String defaultUrl;
    private final String defaultModel;
    private final String defaultReasoningModel;

    LlmProvider(String displayName, String defaultUrl, String defaultModel, String defaultReasoningModel) {
        this.displayName = displayName;
        this.defaultUrl = defaultUrl;
        this.defaultModel = defaultModel;
        this.defaultReasoningModel = defaultReasoningModel;
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

    public String getReasoningModelEnvVar() {
        return this.name() + "_REASONING_MODEL";
    }

    public LlmProvider next() {
        LlmProvider[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }
}
