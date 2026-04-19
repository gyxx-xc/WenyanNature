package indi.wenyan.client.gui.code_editor;

import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Reads the DeepSeek API key from {@code config/WenyanNature/.env}.
 *
 * <p>The file format is a simple key=value pair, one per line.
 * Lines starting with {@code #} are treated as comments.
 *
 * <p>Example {@code .env}:
 * <pre>
 * # DeepSeek API key
 * DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxx
 * </pre>
 */
public final class AiConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiConfig.class);
    private static final String ENV_FILE_RELATIVE = "config/WenyanNature/.env";
    private static final String KEY_NAME = "DEEPSEEK_API_KEY";

    private AiConfig() {}

    /**
     * Returns the API key stored in the .env file, or {@link Optional#empty()} if
     * the file does not exist or the key is not defined.
     */
    public static Optional<String> loadApiKey() {
        Path envPath = getEnvPath();
        if (!Files.exists(envPath)) {
            LOGGER.info("[WenyanNature] .env not found at {}. Create it to use the AI feature.", envPath);
            return Optional.empty();
        }
        try {
            for (String line : Files.readAllLines(envPath)) {
                line = line.strip();
                if (line.startsWith("#") || line.isBlank()) continue;
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String k = line.substring(0, eq).strip();
                String v = line.substring(eq + 1).strip();
                if (KEY_NAME.equals(k) && !v.isEmpty()) {
                    return Optional.of(v);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to read .env: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Returns the expected path of the .env file (relative to the Minecraft game directory).
     */
    public static Path getEnvPath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(ENV_FILE_RELATIVE);
    }

    /**
     * Creates a template .env file at the expected location if it does not already exist.
     */
    public static void createTemplateIfAbsent() {
        Path envPath = getEnvPath();
        if (Files.exists(envPath)) return;
        try {
            Files.createDirectories(envPath.getParent());
            Files.writeString(envPath,
                    "# WenyanNature AI Configuration\n" +
                    "# Fill in your DeepSeek API key below (https://platform.deepseek.com)\n" +
                    KEY_NAME + "=\n");
            LOGGER.info("[WenyanNature] Created template .env at {}", envPath);
        } catch (IOException e) {
            LOGGER.error("[WenyanNature] Failed to create template .env: {}", e.getMessage());
        }
    }
}
