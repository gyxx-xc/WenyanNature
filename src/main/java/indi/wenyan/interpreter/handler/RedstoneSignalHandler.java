package indi.wenyan.interpreter.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanException;
import indi.wenyan.interpreter.utils.WenyanValue;

public class RedstoneSignalHandler extends JavacallHandler {
    private final Thread t;
    private final BlockRunner runner;
    public RedstoneSignalHandler(Thread t, BlockRunner runner) {
        this.t = t;
        this.runner = runner;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        t.interrupt();
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
        return new WenyanValue(WenyanValue.Type.INT, runner.reds, true);
    }
}
