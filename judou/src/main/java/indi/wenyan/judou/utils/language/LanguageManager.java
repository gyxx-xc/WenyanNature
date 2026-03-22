package indi.wenyan.judou.utils.language;

import org.jetbrains.annotations.Nullable;

public enum LanguageManager {
    ;

    @Nullable
    private static ILanguageProvider languageProvider = null;

    public static void registerLanguageProvider(ILanguageProvider provider) {
        if (languageProvider != null)
            throw new IllegalStateException("Language provider already registered");
        languageProvider = provider;
    }

    public static String getTranslation(String key) {
        if (languageProvider == null)
            throw new IllegalStateException("Language provider not registered");
        return languageProvider.getTranslation(key);
    }

    public static String getTranslation(String key, Object... args) {
        if (languageProvider == null)
            throw new IllegalStateException("Language provider not registered");
        return languageProvider.getTranslation(key, args);
    }
}
