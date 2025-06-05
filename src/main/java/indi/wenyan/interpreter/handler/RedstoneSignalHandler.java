package indi.wenyan.interpreter.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;

public class RedstoneSignalHandler extends JavacallHandler {
    private final BlockRunner runner;
    public RedstoneSignalHandler(BlockRunner runner) {
        this.runner = runner;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        int value = 0;
        if (runner.getLevel() != null) {
             value = runner.getLevel().getBestNeighborSignal(runner.getBlockPos());
        }
        return new WenyanValue(WenyanValue.Type.INT, value, true);
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
