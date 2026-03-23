package indi.wenyan.judou.utils.language;

public interface JudouLocalizationEnum {
    String getTranslationKey();

    default String string() {
        return LanguageManager.getTranslation(getTranslationKey());
    }

    default String string(Object... args) {
        return LanguageManager.getTranslation(getTranslationKey(), args);
    }
}
