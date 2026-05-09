package indi.wenyan.judou.api.language;

import indi.wenyan.judou.api.utils.UtilManager;

public interface JudouLocalizationEnum {
    String getTranslationKey();

    default String string() {
        return UtilManager.getLanguage().getTranslation(getTranslationKey());
    }

    default String string(Object... args) {
        return UtilManager.getLanguage().getTranslation(getTranslationKey(), args);
    }
}
