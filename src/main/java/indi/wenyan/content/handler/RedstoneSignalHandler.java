package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;

public class RedstoneSignalHandler implements JavacallHandler {
    public RedstoneSignalHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        int value = 0;
        if (context.runnerWarper().runner() instanceof BlockRunner runner)
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
