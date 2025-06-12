package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;

public class RedstoneSignalHandler implements JavacallHandler {
    private final BlockRunner runner;
    public RedstoneSignalHandler(BlockRunner runner) {
        this.runner = runner;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        int value = 0;
        if (runner.getLevel() != null) {
             value = runner.getLevel().getBestNeighborSignal(runner.getBlockPos());
        }
        return new WenyanNativeValue(WenyanType.INT, value, true);
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
