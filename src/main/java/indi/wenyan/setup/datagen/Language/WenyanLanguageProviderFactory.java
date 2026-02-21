package indi.wenyan.setup.datagen.Language;

import indi.wenyan.WenyanProgramming;
import net.minecraft.data.DataProvider;

public enum WenyanLanguageProviderFactory {;
    public static DataProvider.Factory create(String locale) {
        // going to have more language (at least tranditional chinese)
        //noinspection SwitchStatementWithTooFewBranches
        return switch (locale) {
            case "zh_cn" -> output -> new ChineseLanguageProvider(output, WenyanProgramming.MODID, "zh_cn");
            default -> output -> new EnglishLanguageProvider(output, WenyanProgramming.MODID, "en_us");
        };
    }
}
