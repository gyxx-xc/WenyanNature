package indi.wenyan.judou.utils.language;

import indi.wenyan.judou.utils.UtilManager;

public interface JudouLocalizationEnum {
    String getTranslationKey();

    default String string() {
        return UtilManager.getLanguage().getTranslation(getTranslationKey());
    }

    default String string(Object... args) {
        return UtilManager.getLanguage().getTranslation(getTranslationKey(), args);
    }
}
