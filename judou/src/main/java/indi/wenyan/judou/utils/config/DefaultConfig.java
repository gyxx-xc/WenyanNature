package indi.wenyan.judou.utils.config;

import indi.wenyan.judou.utils.function.ChineseUtils;

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
    public int getMaxQueueSize() {
        return 50;
    }

    @Override
    public int getMaxQueueSizePerTick() {
        return 20;
    }

    @Override
    public boolean useLegacyRunner() {
        return false;
    }

    @Override
    public boolean convertCode() {
        return true;
    }

    @Override
    public ChineseUtils.SymbolFormat symbolConversion() {
        return ChineseUtils.SymbolFormat.TRADITIONAL;
    }
}
