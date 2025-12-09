package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import lombok.Getter;

import java.util.Optional;

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
    public Optional<IWenyanValue> handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        if (getRemainingTicks() <= 0) {
            return Optional.of(handleFinalTick(context));
        }
        if (tickCounter % tickInterval == 0)
            handleTick(context, tickCounter / tickInterval);
        tickCounter ++;
        return Optional.empty();
    }

    abstract protected void handleTick(JavacallContext context, int round) throws WenyanException.WenyanThrowException;
    abstract protected IWenyanValue handleFinalTick(JavacallContext context) throws WenyanException.WenyanThrowException;
}
