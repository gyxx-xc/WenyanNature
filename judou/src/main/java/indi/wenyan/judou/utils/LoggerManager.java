package indi.wenyan.judou.utils;

import org.slf4j.Logger;

public class LoggerManager {
    private static Logger logger = null;

    public static void registerLogger(Logger provider) {
        if (logger != null)
            throw new IllegalStateException("Logger already registered");
        logger = provider;
    }

    public static Logger getLogger() {
        if (logger == null)
            throw new IllegalStateException("Logger not registered");
        return logger;
    }
}
