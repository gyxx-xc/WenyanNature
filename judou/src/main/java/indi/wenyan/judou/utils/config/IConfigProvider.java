package indi.wenyan.judou.utils.config;

import indi.wenyan.judou.utils.function.ChineseUtils;

public interface IConfigProvider {
    int getMaxThread();
    int getMaxSlice();
    int getWatchdogTimeout();
    int getResultMaxSize();
    int getMaxRecursionDepth();

    boolean useLegacyRunner();

    boolean convertCode();

    ChineseUtils.SymbolFormat symbolConvertion();
}
