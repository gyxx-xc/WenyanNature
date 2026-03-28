package indi.wenyan.judou.utils.config;

public interface IConfigProvider {
    int getMaxThread();
    int getMaxSlice();
    int getWatchdogTimeout();
    int getResultMaxSize();
    int getMaxRecursionDepth();

    boolean useLegancyRunner();
}
