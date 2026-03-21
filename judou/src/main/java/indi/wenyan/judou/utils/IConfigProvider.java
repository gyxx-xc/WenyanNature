package indi.wenyan.judou.utils;

public interface IConfigProvider {
    int getMaxThread();
    int getMaxSlice();
    int getWatchdogTimeout();

    int getResultMaxSize();
}
