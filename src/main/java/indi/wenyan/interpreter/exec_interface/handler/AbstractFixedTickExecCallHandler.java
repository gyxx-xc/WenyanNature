package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Deprecated // needed to be review and tested
@SuppressWarnings("ALL")
public abstract class AbstractFixedTickExecCallHandler implements IExecCallHandler {
    private int tickCounter = 0;
    @Getter
    private final int tickInterval;
    @Getter
    private final int totalTicks;
    public int getRemainingTicks() {
        return totalTicks - tickCounter;
    }

    protected AbstractFixedTickExecCallHandler(int totalTicks) {
        this(1, totalTicks);
    }

    protected AbstractFixedTickExecCallHandler(int tickInterval, int totalTicks) {
        this.tickInterval = tickInterval;
        this.totalTicks = totalTicks;
    }

    @Override
    public boolean handle(@NotNull IHandleContext context, @NotNull JavacallRequest request) throws WenyanException.WenyanThrowException {
        if (getRemainingTicks() <= 0) {
            request.thread().currentRuntime().processStack.push(handleFinalTick(request));
            return true;
        }
        if (tickCounter % tickInterval == 0)
            handleTick(request, tickCounter / tickInterval);
        tickCounter ++;
        return false;
    }
    abstract protected void handleTick(JavacallRequest request, int round) throws WenyanException.WenyanThrowException;
    abstract protected IWenyanValue handleFinalTick(JavacallRequest reqest) throws WenyanException.WenyanThrowException;
}
