package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.executor.WenyanCode;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanRunner implements IWenyanRunner {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    @Getter
    @NotNull
    private WenyanRuntime currentRuntime;

    @Getter
    private final WenyanPackage globals;

    private boolean willPause = false;

    private int recursionDepth = 0;

    private WenyanRunner(@NotNull WenyanRuntime mainRuntime, WenyanPackage globals) {
        this.globals = globals;
        currentRuntime = mainRuntime;
    }

    @Contract("_, _ -> new")
    public static @NotNull IWenyanRunner of(WenyanRuntime mainRuntime, IWenyanRunner runner) {
        return new WenyanRunner(mainRuntime, runner.getGlobals());
    }

    @Contract("_, _ -> new")
    public static @NotNull IWenyanRunner of(WenyanRuntime mainRuntime, WenyanPackage globals) {
        return new WenyanRunner(mainRuntime, globals);
    }

    @Override
    public void run(int step) {
        willPause = false;
        for (int i = 0; i < step; i++) {
            try {
                WenyanRuntime runtime = getCurrentRuntime();
                if (validateRuntimeState(runtime)) return;
                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.programCounter);
                WenyanCode code = bytecode.code().getCode();
                consumeStep(1);
                code.exec(bytecode.arg(), this);

                if (updateProgramCounter(runtime)) return;
            } catch (WenyanException e) {
                dieWithException(this, e);
                return;
            } catch (RuntimeException e) { // for any other missing exceptions
                dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
                return;
            }
        }
        try {
            this.yield();
        } catch (WenyanException e) {
            dieWithException(this, e);
        }
    }

    private boolean updateProgramCounter(WenyanRuntime runtime) throws WenyanUnreachedException {
        if (!runtime.PCFlag)
            runtime.programCounter++;
        runtime.PCFlag = false;

        return willPause;
    }

    private boolean validateRuntimeState(WenyanRuntime runtime) {
        if (runtime.programCounter < 0 || runtime.programCounter >= runtime.getBytecode().size()) {
            dieWithException(this, new WenyanUnreachedException());
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        willPause = true;
    }

    public static void dieWithException(IWenyanRunner runner, WenyanException e) {
        Logger logger = LoggerManager.getLogger();
        e.handle(runner.platform()::handleError, logger,
                runner.getCurrentRuntime().getErrorContext(e, logger));
        try {
            runner.die();
        } catch (WenyanUnreachedException e1) {
            logger.error("Unexpected, failed to die");
        }
    }

    @Override
    public void call(WenyanRuntime runtime) {
        recursionDepth++;
        if (recursionDepth > 3000) {
            dieWithException(this, new WenyanException("递归深度过深"));
            return;
        }
        currentRuntime = runtime;
    }

    @Override
    public void ret() throws WenyanUnreachedException {
        recursionDepth--;
        var returnRuntime = currentRuntime.getReturnRuntime();
        if (returnRuntime == null) {
            die();
        } else {
            currentRuntime = returnRuntime;
        }
    }
}
