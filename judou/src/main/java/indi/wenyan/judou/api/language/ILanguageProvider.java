package indi.wenyan.judou.api.language;

/// Provides localized translations for Wenyan error messages and type names.
public interface ILanguageProvider {
    String getTranslation(String key);

    default String getTranslation(String key, @SuppressWarnings("unused") Object... args) {
        return getTranslation(key);
    }
}
