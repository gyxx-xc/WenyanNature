package indi.wenyan.content.handler;

import indi.wenyan.content.block.runner.RunnerBlockEntity;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
public class RedstoneSignalHandler implements IExecCallHandler {

    @Override
    public IWenyanValue handle(JavacallContext context) {
        int value = 0;
        if (context.runnerWarper().runner() instanceof RunnerBlockEntity runner)
            if (runner.getLevel() != null) {
                value = runner.getLevel().getBestNeighborSignal(runner.getBlockPos());
            }
        return new WenyanInteger(value);
    }
}
