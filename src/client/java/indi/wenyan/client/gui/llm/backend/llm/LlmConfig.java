package indi.wenyan.client.gui.llm.backend.llm;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reads AI model configuration from {@code config/WenyanNature/.env}.
 */
public final class LlmConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmConfig.class);
    private static final String ENV_FILE_RELATIVE = "config/WenyanNature/.env";

    private static final Map<String, String> ENV_CACHE = new HashMap<>();
    private static boolean loaded = false;
    private static boolean templateChecked = false;

    private LlmConfig() {
    }

    /** Reloads properties from .env */
    public static void reload() {
        ENV_CACHE.clear();
        loaded = true;

        Path envPath = getEnvPath();
        if (!Files.exists(envPath))
            return;

        try {
            for (String line : Files.readAllLines(envPath)) {
                parseLine(line).ifPresent(entry -> ENV_CACHE.put(entry.key(), entry.value()));
            }
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to read .env: {}", e.getMessage());
        }
    }

    public static String get(String key) {
        if (!loaded)
            reload();
        return ENV_CACHE.get(key);
    }

    public static Optional<String> getOptional(String key) {
        return Optional.ofNullable(get(key));
    }

    private static Optional<EnvEntry> parseLine(String line) {
        line = line.strip();
        if (line.startsWith("#") || line.isBlank())
            return Optional.empty();
        if (line.startsWith("export ")) {
            line = line.substring("export ".length()).strip();
        }

        int eq = line.indexOf('=');
        if (eq < 0)
            return Optional.empty();

        String key = line.substring(0, eq).strip();
        String value = stripValue(line.substring(eq + 1).strip());
        if (key.isEmpty() || value.isEmpty())
            return Optional.empty();
        return Optional.of(new EnvEntry(key, value));
    }

    private static String stripValue(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    public static Path getEnvPath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(ENV_FILE_RELATIVE);
    }

    public static void createTemplateIfAbsent() {
        if (templateChecked)
            return;
        Path envPath = getEnvPath();
        if (Files.exists(envPath)) {
            appendMissingProviderEntries(envPath);
            templateChecked = true;
            return;
        }
        try {
            Files.createDirectories(envPath.getParent());

            StringBuilder sb = new StringBuilder();
            sb.append("# 吾有一术 LLM 大模型配置\n");
            sb.append("# 在下方填写想用的模型的API,和base_URL（可选）和模型（可选）\n");
            sb.append("# 重要警告: 此文件的密钥可以让任何人直接访问你的API, 并进行扣费使用, 确保不要将此文件分享给任何人!");
            sb.append("\n");
            for (LlmProvider provider : LlmProvider.values()) {
                sb.append("# --- ").append(provider.getDisplayName()).append(" ---\n");
                sb.append(provider.getApiKeyEnvVar()).append("=\n");
                appendOptionalEntry(sb, provider.getUrlEnvVar(), provider.getDefaultUrl());
                appendOptionalEntry(sb, provider.getModelEnvVar(), provider.getDefaultModel());
                appendOptionalEntry(sb, provider.getReasoningModelEnvVar(), provider.getDefaultReasoningModel());
                sb.append("\n");
            }

            Files.writeString(envPath, sb.toString());
            templateChecked = true;
            LOGGER.info("[WenyanNature] Created template .env at {}", envPath);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to create template .env: {}", e.getMessage());
        }
    }

    private static void appendMissingProviderEntries(Path envPath) {
        try {
            List<String> lines = Files.readAllLines(envPath);
            StringBuilder sb = new StringBuilder();
            appendMissingCustomEntries(lines, sb);
            for (LlmProvider provider : LlmProvider.values()) {
                if (provider == LlmProvider.CUSTOM) {
                    continue;
                }
                String key = provider.getReasoningModelEnvVar();
                if (!containsEnvKey(lines, key)) {
                    if (sb.isEmpty()) {
                        String content = Files.readString(envPath);
                        if (!content.isEmpty() && !content.endsWith("\n") && !content.endsWith("\r")) {
                            sb.append("\n");
                        }
                        sb.append("\n# --- Reasoning model presets ---\n");
                    }
                    appendOptionalEntry(sb, key, provider.getDefaultReasoningModel());
                }
            }
            if (!sb.isEmpty()) {
                Files.writeString(envPath, sb.toString(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to update template .env: {}", e.getMessage());
        }
    }

    private static void appendMissingCustomEntries(List<String> lines, StringBuilder sb) {
        LlmProvider provider = LlmProvider.CUSTOM;
        boolean missingApiKey = !containsEnvKey(lines, provider.getApiKeyEnvVar());
        boolean missingUrl = !containsEnvKey(lines, provider.getUrlEnvVar());
        boolean missingModel = !containsEnvKey(lines, provider.getModelEnvVar());
        boolean missingReasoningModel = !containsEnvKey(lines, provider.getReasoningModelEnvVar());
        if (!missingApiKey && !missingUrl && !missingModel && !missingReasoningModel) {
            return;
        }

        sb.append("\n# --- ").append(provider.getDisplayName()).append(" ---\n");
        if (missingApiKey) {
            sb.append(provider.getApiKeyEnvVar()).append("=\n");
        }
        if (missingUrl) {
            appendOptionalEntry(sb, provider.getUrlEnvVar(), provider.getDefaultUrl());
        }
        if (missingModel) {
            appendOptionalEntry(sb, provider.getModelEnvVar(), provider.getDefaultModel());
        }
        if (missingReasoningModel) {
            appendOptionalEntry(sb, provider.getReasoningModelEnvVar(), provider.getDefaultReasoningModel());
        }
    }

    private static boolean containsEnvKey(List<String> lines, String key) {
        for (String line : lines) {
            line = line.strip();
            if (line.startsWith("#")) {
                line = line.substring(1).strip();
            }
            if (line.startsWith("export ")) {
                line = line.substring("export ".length()).strip();
            }
            int eq = line.indexOf('=');
            if (eq >= 0 && line.substring(0, eq).strip().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private static void appendOptionalEntry(StringBuilder sb, String key, String defaultValue) {
        sb.append("#").append(key).append("=");
        if (!defaultValue.isBlank()) {
            sb.append(defaultValue);
        }
        sb.append("\n");
    }

    private record EnvEntry(String key, String value) {
    }
}
