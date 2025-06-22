package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.WenyanInteger;
import indi.wenyan.interpreter.structure.values.WenyanNativeValue;

public class RedstoneSignalHandler implements JavacallHandler {
    public RedstoneSignalHandler() {
    }

    @Override
    public WenyanNativeValue handle(JavacallContext context) {
        int value = 0;
        if (context.runnerWarper().runner() instanceof BlockRunner runner)
            if (runner.getLevel() != null) {
                value = runner.getLevel().getBestNeighborSignal(runner.getBlockPos());
            }
        return new WenyanNativeValue(WenyanInteger.TYPE, value, true);
    }
    @Override
    public boolean isLocal(JavacallContext context) {
        return false;
    }
}
