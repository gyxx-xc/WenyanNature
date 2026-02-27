package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.executor.WenyanCode;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.WenyanCompileException;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanThread implements IThreadHolder<WenyanProgramImpl.PCB> {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    /**
     * Stack of runtime environments
     */
    private final Deque<WenyanRuntime> runtimes = new ArrayDeque<>();

    @Getter
    private final WenyanRuntime mainRuntime;

    private boolean willPause = false;

    private WenyanThread(WenyanRuntime mainRuntime) {
        this.mainRuntime = mainRuntime;
    }

    public static @NotNull WenyanThread ofCode(String code, WenyanRuntime basicRuntime) throws WenyanCompileException {
        var bytecode = new WenyanBytecode(code);
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode);
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        visitor.visit(WenyanVisitor.program(code));
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        WenyanVerifier.verify(bytecode);

        return ofRuntime(new WenyanRuntime(bytecode), basicRuntime);
    }

    public static @NotNull WenyanThread ofRuntime(WenyanRuntime mainRuntime, WenyanRuntime basicRuntime) throws WenyanCompileException {
        WenyanThread thread = new WenyanThread(mainRuntime);
        thread.call(basicRuntime);
        thread.call(mainRuntime);
        return thread;
    }

    @Override
    public void run(int step) {
        willPause = false;
        for (int i = 0; i < step && !willPause; i++) {
            try {
                WenyanRuntime runtime = currentRuntime();
                if (validateRuntimeState(runtime)) return;

                assert runtime.getBytecode() != null; //checked by validateRuntimeState
                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.programCounter);
                WenyanCode code = bytecode.code().getCode();
                int needStep = code.getStep(bytecode.arg(), this);
                consumeStep(needStep);
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

    private boolean updateProgramCounter(WenyanRuntime runtime) {
        if (getMainRuntime().finishFlag) {
            safeDie();
            return true;
        }

        if (!runtime.PCFlag)
            runtime.programCounter++;
        runtime.PCFlag = false;

        return false;
    }

    private boolean validateRuntimeState(WenyanRuntime runtime) {
        if (runtime.getBytecode() == null) {
            dieWithException(new WenyanUnreachedException());
            return true;
        }
        if (runtime.programCounter < 0 || runtime.programCounter >= runtime.getBytecode().size()) {
            dieWithException(new WenyanUnreachedException());
            return true;
        }
        return false;
    }

    private void safeDie() {
        Logger logger = LoggerManager.getLogger();
        try {
            die();
        } catch (WenyanUnreachedException e) {
            logger.error("Unexpected, failed to die after handling an exception", e);
        }
    }

    @Override
    public void pause() {
        willPause = true;
    }

    public void dieWithException(WenyanException e) {
        var runtime = currentRuntime();
        Logger logger = LoggerManager.getLogger();
        WenyanException.ErrorContext errorContext = null;
        try {
            if (runtime != null && runtime.getBytecode() != null) {
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
        safeDie();
    }

    /**
     * Adds a runtime environment to the top of the stack.
     *
     * @param runtime The runtime to add
     */
    public void call(WenyanRuntime runtime) {
        runtimes.push(runtime);
    }

    /**
     * Removes the top runtime environment from the stack.
     */
    public void ret() {
        var runtime = runtimes.pop();
        runtime.finishFlag = true;
    }

    /**
     * Gets the current runtime environment from the top of the stack.
     *
     * @return The current runtime environment
     */
    public WenyanRuntime currentRuntime() {
        return runtimes.peek();
    }

    /**
     * Searches for a variable in all runtime environments, from top to bottom.
     *
     * @param id The variable identifier
     * @return The variable value
     * @throws WenyanException If the variable is not found
     */
    public IWenyanValue getGlobalVariable(String id) throws WenyanException {
        IWenyanValue value = null;
        // TODO: use closure or at least a cache
        for (var runtime : runtimes) {
            if (runtime.getVariables().containsKey(id)) {
                value = runtime.getVariables().get(id);
                break;
            }
        }
        if (value == null)
            throw new WenyanException(LanguageManager.getTranslation("error.wenyan_programming.variable_not_found_") + id);
        return value;
    }

    public int runtimeSize() {
        return runtimes.size();
    }

    private boolean willDie = false;

    @Override
    public void die() throws WenyanUnreachedException {
        willDie = true;
        IThreadHolder.super.die();
    }

    public boolean isDying() {
        return willDie;
    }
}
