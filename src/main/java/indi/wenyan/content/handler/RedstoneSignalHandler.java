package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
public class RedstoneSignalHandler implements IJavacallHandler {

    @Override
    public IWenyanValue handle(JavacallContext context) {
        int value = 0;
        if (context.runnerWarper().runner() instanceof BlockRunner runner)
            if (runner.getLevel() != null) {
                value = runner.getLevel().getBestNeighborSignal(runner.getBlockPos());
            }
        return new WenyanInteger(value);
    }
}
