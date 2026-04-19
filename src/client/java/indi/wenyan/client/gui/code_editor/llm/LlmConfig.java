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

    private LlmConfig() {}

    /** Reloads properties from .env */
    public static void reload() {
        ENV_CACHE.clear();
        loaded = true;

        Path envPath = getEnvPath();
        if (!Files.exists(envPath)) return;

        try {
            for (String line : Files.readAllLines(envPath)) {
                line = line.strip();
                if (line.startsWith("#") || line.isBlank()) continue;
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String k = line.substring(0, eq).strip();
                String v = line.substring(eq + 1).strip();
                if (!v.isEmpty()) {
                    ENV_CACHE.put(k, v);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to read .env: {}", e.getMessage());
        }
    }

    public static String get(String key) {
        if (!loaded) reload();
        return ENV_CACHE.get(key);
    }

    public static Optional<String> getOptional(String key) {
        return Optional.ofNullable(get(key));
    }

    public static Path getEnvPath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(ENV_FILE_RELATIVE);
    }

    public static void createTemplateIfAbsent() {
        Path envPath = getEnvPath();
        if (Files.exists(envPath)) return;
        try {
            Files.createDirectories(envPath.getParent());

            StringBuilder sb = new StringBuilder();
            sb.append("# WenyanNature AI Configuration\n");
            sb.append("# Fill in the API key corresponding to the model you want to use.\n");
            sb.append("# By default, the default URL and MODEL will be used if omitted.\n\n");

            for (LlmProvider provider : LlmProvider.values()) {
                sb.append("# --- ").append(provider.getDisplayName()).append(" ---\n");
                sb.append(provider.getApiKeyEnvVar()).append("=\n");
                sb.append("#").append(provider.getUrlEnvVar()).append("=").append(provider.getDefaultUrl()).append("\n");
                sb.append("#").append(provider.getModelEnvVar()).append("=").append(provider.getDefaultModel()).append("\n\n");
            }

            Files.writeString(envPath, sb.toString());
            LOGGER.info("[WenyanNature] Created template .env at {}", envPath);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to create template .env: {}", e.getMessage());
        }
    }
}
