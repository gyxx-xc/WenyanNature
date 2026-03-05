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
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanRunner implements IThreadHolder<WenyanProgramImpl.PCB> {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    @Getter
    private WenyanRuntime currentRuntime;
    @Nullable
    private final List<String> exportedIdentifier;
    /// if value is null, means the exported function is not finished
    @Getter
    @Nullable
    private WenyanPackage exportedPackage = null;

    private final WenyanPackage globals;

    private boolean willPause = false;

    private WenyanRunner(WenyanRuntime mainRuntime, WenyanPackage globals, @Nullable List<String> exportedIdentifier) {
        this.exportedIdentifier = exportedIdentifier;
        this.globals = globals;
        call(mainRuntime);
    }

    public static @NotNull WenyanRunner ofCode(String code, WenyanPackage basicRuntime) throws WenyanCompileException {
        var bytecode = new WenyanBytecode(code);
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode, null, Collections.emptyList());
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        visitor.visit(WenyanVisitor.program(code));
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        WenyanVerifier.verify(bytecode);

        return new WenyanRunner(new WenyanRuntime(bytecode), basicRuntime, environment.getExportedValues());
    }

    public WenyanRunner forkRuntime(WenyanRuntime mainRuntime) {
        return new WenyanRunner(mainRuntime, globals, null);
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

    /**
     * Adds a runtime environment to the top of the stack.
     *
     * @param runtime The runtime to add
     */
    public void call(WenyanRuntime runtime) {
        currentRuntime = runtime;
    }

    /**
     * Removes the top runtime environment from the stack.
     */
    public void ret() throws WenyanUnreachedException {
        var returnRuntime = currentRuntime.getReturnRuntime();
        if (returnRuntime == null) {
            die();
            if (exportedIdentifier == null) return;
            Map<String, IWenyanValue> result = new HashMap<>();
            for (int i = 0; i < exportedIdentifier.size(); i++) {
                result.put(exportedIdentifier.get(i), currentRuntime.getLocals().get(i));
            }
            exportedPackage = new WenyanPackage(result);
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
