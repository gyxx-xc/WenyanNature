package indi.wenyan.judou.api.language;

public interface ILanguageProvider {
    String getTranslation(String key);

    default String getTranslation(String key, @SuppressWarnings("unused") Object... args) {
        return getTranslation(key);
    }
}
