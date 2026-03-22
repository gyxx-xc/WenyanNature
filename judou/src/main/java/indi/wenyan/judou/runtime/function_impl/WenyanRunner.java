package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.executor.WenyanCode;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.WenyanThreading;
import indi.wenyan.judou.utils.language.ExceptionText;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
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

    @Getter
    @NotNull
    private WenyanFrame currentRuntime;

    @Getter
    private final IGlobalResolver globals;

    private boolean willPause = false;

    private int recursionDepth = 0;

    private WenyanRunner(@NotNull WenyanFrame mainRuntime, IGlobalResolver globals) {
        this.globals = globals;
        currentRuntime = mainRuntime;
    }

    @Contract("_, _ -> new")
    public static @NotNull IWenyanRunner of(WenyanFrame mainRuntime, IWenyanRunner runner) {
        return new WenyanRunner(mainRuntime, runner.getGlobals());
    }

    @Contract("_, _ -> new")
    public static @NotNull IWenyanRunner of(WenyanFrame mainRuntime, IGlobalResolver globals) {
        return new WenyanRunner(mainRuntime, globals);
    }

    @Override
    public void call(WenyanFrame runtime) {
        recursionDepth++;
        if (recursionDepth > 3000) {
            IWenyanRunner.dieWithException(this, new WenyanException(ExceptionText.RecursionDepthTooDeep.string()));
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


    @Override
    public void run(int step) {
        willPause = false;
        for (int i = 0; i < step; i++) {
            try {
                WenyanFrame runtime = currentRuntime;
                if (validateRuntimeState(runtime)) return;
                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.getProgramCounter());
                WenyanCode code = bytecode.code().getCode();
                consumeStep(1);
                code.exec(bytecode.arg(), this);

                if (updateProgramCounter(runtime)) return;
            } catch (WenyanException e) {
                IWenyanRunner.dieWithException(this, e);
                return;
            } catch (RuntimeException e) { // for any other missing exceptions
                IWenyanRunner.dieWithException(this, new WenyanUnreachedException.WenyanUnexceptedException(e));
                return;
            }
        }
        try {
            this.yield();
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(this, e);
        }
    }

    private boolean updateProgramCounter(WenyanFrame runtime) throws WenyanUnreachedException {
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
