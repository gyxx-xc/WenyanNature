package indi.wenyan.judou.api.utils;

public interface IConfigProvider {
    int getMaxThread();
    int getMaxSlice();
    int getWatchdogTimeout();
    int getResultMaxSize();
    int getMaxRecursionDepth();

    int getMaxQueueSize();
    int getMaxQueueSizePerTick();

    boolean useLegacyRunner();

    boolean convertCode();

    ChineseUtils.SymbolFormat symbolConversion();
}
