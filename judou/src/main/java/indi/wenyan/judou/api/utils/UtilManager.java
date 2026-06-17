package indi.wenyan.judou.api.utils;

import indi.wenyan.judou.api.language.ILanguageProvider;
import indi.wenyan.judou.utils.DefaultConfig;
import indi.wenyan.judou.utils.RawLanguageProvider;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

/// Global manager for logger, config, and language singletons.
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
