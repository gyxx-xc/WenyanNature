package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.executor.WenyanCode;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
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
public class WenyanRunner implements IThreadHolder<WenyanProgramImpl.PCB>, IFrameManager<WenyanRuntime> {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    @Getter
    private WenyanRuntime currentRuntime;

    private final WenyanPackage globals;

    private boolean willPause = false;

    private int recursionDepth = 0;

    private WenyanRunner(WenyanRuntime mainRuntime, WenyanPackage globals) {
        this.globals = globals;
        call(mainRuntime);
    }

    @Deprecated
    public static @NotNull WenyanRunner ofCode(String code, WenyanPackage basicRuntime) throws WenyanCompileException {
        // TODO: refactor test
        return new WenyanRunner(WenyanRuntime.ofCode(code), basicRuntime);
    }

    @Contract("_, _ -> new")
    public static @NotNull WenyanRunner of(WenyanRuntime mainRuntime, WenyanRunner runner) {
        return new WenyanRunner(mainRuntime, runner.globals);
    }

    @Contract("_, _ -> new")
    public static @NotNull WenyanRunner of(WenyanRuntime mainRuntime, WenyanPackage globals) {
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
                dieWithException(e);
                return;
            } catch (RuntimeException e) { // for any other missing exceptions
                dieWithException(new WenyanUnreachedException.WenyanUnexceptedException(e));
                return;
            }
        }
        try {
            this.yield();
        } catch (WenyanException e) {
            dieWithException(e);
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
            dieWithException(new WenyanUnreachedException());
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        willPause = true;
    }

    public void dieWithException(WenyanException e) {
        var runtime = getCurrentRuntime();
        Logger logger = LoggerManager.getLogger();
        WenyanException.ErrorContext errorContext = null;
        try {
            if (runtime != null) {
                WenyanBytecode.Context context = runtime.getBytecode().getContext(runtime.programCounter - 1);
                errorContext = new WenyanException.ErrorContext(
                        context.line(), context.column(),
                        runtime.getBytecode().getSourceCode().substring(context.contentStart(), context.contentEnd()));
            }
        } catch (WenyanException.WenyanVarException |
                 IndexOutOfBoundsException ignore) {// cause error context be null, handled below
        }
        if (errorContext == null)
            logger.error("Unexpected, failed to get code context during handling an exception", e);
        e.handle(platform()::handleError, logger, errorContext);
        try {
            die();
        } catch (WenyanUnreachedException e1) {
            logger.error("Unexpected, failed to die");
        }
    }

    @Override
    public void call(WenyanRuntime runtime) {
        recursionDepth ++;
        if (recursionDepth > 3000) {
            dieWithException(new WenyanException("递归深度过深"));
            return;
        }
        currentRuntime = runtime;
    }

    @Override
    public void ret() throws WenyanUnreachedException {
        recursionDepth --;
        var returnRuntime = currentRuntime.getReturnRuntime();
        if (returnRuntime == null) {
            die();
        } else {
            currentRuntime = returnRuntime;
        }
    }

    /**
     * Searches for a variable in all runtime environments, from top to bottom.
     *
     * @param id The variable identifier
     * @return The variable value
     * @throws WenyanException If the variable is not found
     */
    public IWenyanValue getGlobalVariable(String id) throws WenyanException {
        return globals.getAttribute(id);
    }
}
