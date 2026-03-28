package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanRunner<T extends IWenyanThread> implements IWenyanRunner, IThreadHolder<T> {
    @Getter
    @Setter
    private T thread;

    @Getter private final IGlobalResolver globalResolver;
    @Getter private final FrameManagerImpl frameManager;

    private boolean willPause = false;

    public WenyanRunner(@NotNull WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
        frameManager = new FrameManagerImpl(mainRuntime);
    }

    @Override
    public int run(int step) {
        willPause = false;
        int i = 0;
        try {
            for (; i < step; i++) {
                WenyanFrame runtime = getFrameManager().getNullableCurrentRuntime();
                if (runtime == null) { // reach return of main
                    die();
                    return i;
                }
                if (validateRuntimeState(runtime)) return i;

                int programCounter = runtime.getProgramCounter();
                int arg = runtime.getBytecode().getArg(programCounter);
                var code = runtime.getBytecode().getCode(programCounter);
                code.getCode().exec(arg, this);

                if (updateProgramCounter(runtime)) return i + 1;
            }
            this.yield();
            return step;
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(this, e);
            return i + 1; // might i here, but it's not a big deal
        } catch (RuntimeException e) { // for any other missing exceptions
            IWenyanRunner.dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
            return i + 1;
        }
    }

    private boolean updateProgramCounter(WenyanFrame runtime) {
        if (!runtime.isPCFlag())
            runtime.setProgramCounter(runtime.getProgramCounter() + 1);
        runtime.setPCFlag(false);

        return willPause;
    }

    private boolean validateRuntimeState(WenyanFrame runtime) {
        if (runtime.getProgramCounter() < 0 || runtime.getProgramCounter() >= runtime.getBytecode().size()) {
            IWenyanRunner.dieWithException(this, new WenyanUnreachedException());
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        willPause = true;
    }
}
