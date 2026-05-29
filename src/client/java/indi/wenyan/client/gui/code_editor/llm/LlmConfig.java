package indi.wenyan.client.gui.code_editor.llm;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
        Path envPath = getEnvPath();
        if (Files.exists(envPath))
            return;
        try {
            Files.createDirectories(envPath.getParent());

            StringBuilder sb = new StringBuilder();
            sb.append("# 吾有一术 LLM 大模型配置\n");
            sb.append("# 在下方填写想用的模型的API,和base_URL（可选）和模型（可选）\n");
            for (LlmProvider provider : LlmProvider.values()) {
                sb.append("# --- ").append(provider.getDisplayName()).append(" ---\n");
                sb.append(provider.getApiKeyEnvVar()).append("=\n");
                sb.append("#").append(provider.getUrlEnvVar()).append("=").append(provider.getDefaultUrl())
                        .append("\n");
                sb.append("#").append(provider.getModelEnvVar()).append("=").append(provider.getDefaultModel())
                        .append("\n\n");
            }

            Files.writeString(envPath, sb.toString());
            LOGGER.info("[WenyanNature] Created template .env at {}", envPath);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to create template .env: {}", e.getMessage());
        }
    }

    private record EnvEntry(String key, String value) {
    }
}
