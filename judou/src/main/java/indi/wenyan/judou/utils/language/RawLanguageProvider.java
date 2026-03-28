package indi.wenyan.judou.utils.language;

import java.util.Arrays;

public class RawLanguageProvider implements ILanguageProvider {
    @Override
    public String getTranslation(String key) {
        return key;
    }

    @Override
    public String getTranslation(String key, Object... args) {
        return key + " " + Arrays.toString(args);
    }
}
