package indi.wenyan.judou.utils;

import org.jetbrains.annotations.Nullable;

public class LanguageManager {
    @Nullable
    private static ILanguageProvider languageProvider = null;
    private LanguageManager() {}
    public static void registerLanguageProvider(ILanguageProvider provider) {
        languageProvider = provider;
    }

    public static String getTranslation(String key) {
        if (languageProvider == null)
            throw new IllegalStateException("Language provider not registered");
        return languageProvider.getTranslation(key);
    }
}
