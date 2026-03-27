package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.IGlobalResolver;
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
public class WenyanRunner implements IWenyanRunner {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    @Getter private final IGlobalResolver globalResolver;
    @Getter private final FrameManagerImpl frameManager;

    private boolean willPause = false;

    public WenyanRunner(@NotNull WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
        frameManager = new FrameManagerImpl(mainRuntime);
    }

    @Override
    public void run(int step) {
        willPause = false;
        try {
            for (int i = 0; i < step; i++) {
                WenyanFrame runtime = getFrameManager().getNullableCurrentRuntime();
                if (runtime == null) { // reach return of main
                    die();
                    return;
                }
                if (validateRuntimeState(runtime)) return;

                consumeStep(1);
                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.getProgramCounter());
                bytecode.code().getCode().exec(bytecode.arg(), this);

                if (updateProgramCounter(runtime)) return;
            }
            this.yield();
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(this, e);
        } catch (RuntimeException e) { // for any other missing exceptions
            IWenyanRunner.dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
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
