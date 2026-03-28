package indi.wenyan.judou.utils;

import indi.wenyan.judou.utils.config.DefaultConfig;
import indi.wenyan.judou.utils.config.IConfigProvider;
import indi.wenyan.judou.utils.language.ILanguageProvider;
import indi.wenyan.judou.utils.language.RawLanguageProvider;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

@SuppressWarnings("NonFinalFieldInEnum")
public enum UtilManager {
    ;

    @Setter @Getter
    private static Logger logger = NOPLogger.NOP_LOGGER;
    @Setter @Getter
    private static IConfigProvider config = new DefaultConfig();
    @Setter @Getter
    private static ILanguageProvider language = new RawLanguageProvider();
}
