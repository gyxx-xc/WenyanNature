package indi.wenyan.judou.utils.config;

public class DefaultConfig implements IConfigProvider {
    @Override
    public int getMaxThread() {
        return 10;
    }

    @Override
    public int getMaxSlice() {
        return 1000;
    }

    @Override
    public int getWatchdogTimeout() {
        return 10;
    }

    @Override
    public int getResultMaxSize() {
        return 64;
    }

    @Override
    public int getMaxRecursionDepth() {
        return 3000;
    }

    @Override
    public boolean useLegancyRunner() {
        return false;
    }
}
