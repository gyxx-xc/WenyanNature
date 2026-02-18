package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;
import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.executor.WenyanCodes;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanParseTreeException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanThread  implements IThreadHolder<WenyanProgramImpl.PCB> {
    @Getter
    @Setter
    private WenyanProgramImpl.PCB thread;

    private final String code;

    private boolean willPause = false;

    /**
     * Stack of runtime environments
     */
    private final Deque<WenyanRuntime> runtimes = new ArrayDeque<>();

    private WenyanRuntime mainRuntime;

    private WenyanThread(String code) {
        this.code = code;
    }

    public static @NotNull WenyanThread ofCode(String code, IWenyanPlatform platform) throws WenyanThrowException {
        WenyanThread thread = new WenyanThread(code);
        thread.call(platform.initEnvironment());

        var bytecode = new WenyanBytecode();
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode);
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        try {
            visitor.visit(WenyanVisitor.program(code));
        } catch (WenyanParseTreeException e) {
            throw new WenyanException(e.getMessage());
        }
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        WenyanVerifier.verify(bytecode);

        // call main
        WenyanRuntime mainRuntime = new WenyanRuntime(bytecode);
        thread.call(mainRuntime);
        thread.mainRuntime = mainRuntime;
        return thread;
    }

    int temp = 0;
    @Override
    public void run(int step) {
        for (int i = 0; i < step; i++) {
            try {
                if (getMainRuntime().finishFlag) {
                    die();
                    return;
                }

                WenyanRuntime runtime = currentRuntime();
                if (runtime.getBytecode() == null) {
                    dieWithException(new WenyanException.WenyanUnreachedException());
                    return;
                }

                if (runtime.programCounter < 0 || runtime.programCounter >= runtime.getBytecode().size()) {
                    dieWithException(new WenyanException.WenyanUnreachedException());
                    return;
                }

                WenyanBytecode.Code bytecode = runtime.getBytecode().get(runtime.programCounter);
                int needStep = bytecode.code().getCode().getStep(bytecode.arg(), this);

                consumeStep(needStep);

                bytecode.code().getCode().exec(bytecode.arg(), this);
                if (!runtime.PCFlag)
                    runtime.programCounter++;
                runtime.PCFlag = false;

                if (willPause) {
                    willPause = false;
                    return;
                }
            } catch (Exception e) {
                dieWithException(e);
                // rethrow interrupt
                if (e instanceof InterruptedException ie)
                    Thread.interrupted();
                return;
            }
        }
        try {
            this.yield();
        } catch (WenyanException e) {
            dieWithException(e);
        }
    }

    @Override
    public void pause() {
        willPause = true;
    }

    public void dieWithException(Exception e) {
        try {
            var runtime = currentRuntime();
            if (runtime.getBytecode() == null) {
                LoggerManager.getLogger().error("during handling an exception", e);
                platform().handleError("WenyanThread died with an unexpected exception, killed");
                return;
            }
            switch (e) {
                case WenyanException.WenyanUnreachedException ignored -> {
                    LoggerManager.getLogger().error("WenyanThread died with an unexpected exception", e);
                    LoggerManager.getLogger().debug("{}", code);
                    WenyanBytecode.Context context =
                            runtime.getBytecode().getContext(runtime.programCounter - 1);
                    String segment = code.substring(context.contentStart(), context.contentEnd());
                    LoggerManager.getLogger().error("{}:{} {}", context.line(), context.column(), segment);
                    platform().handleError("WenyanThread died with an unexpected exception, killed");
                }
                case WenyanException ignored -> {
                    WenyanBytecode.Context context = runtime.getBytecode().getContext(runtime.programCounter);
                    platform().handleError(context.line() + ":" + context.column() + " " +
                            code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                }
                case WenyanThrowException ignored -> {
                    WenyanBytecode.Context context =
                            runtime.getBytecode().getContext(runtime.programCounter - 1);
                    platform().handleError(context.line() + ":" + context.column() + " " +
                            code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                }
                case null, default ->
                        throw new WenyanException.WenyanUnreachedException(); // catch by outside
            }
        } catch (Exception unexpected) {
            LoggerManager.getLogger().error("during handling an exception", e);
            LoggerManager.getLogger().error("WenyanThread died with an unexpected exception", unexpected);
            platform().handleError("WenyanThread died with an unexpected exception, killed");
        }
        try {
            die();
        } catch (WenyanException ex) {
            LoggerManager.getLogger().error("during handling an exception", e);
            LoggerManager.getLogger().error("WenyanThread died with an unexpected exception", ex);
            platform().handleError("WenyanThread died with an unexpected exception, killed");
        }
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
    public IWenyanValue getGlobalVariable(String id) throws WenyanThrowException {
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

    public WenyanRuntime getMainRuntime() {
        return mainRuntime;
    }

    private boolean willDie = false;

    @Override
    public void die() throws WenyanException {
        willDie = true;
        IThreadHolder.super.die();
    }

    public boolean isDying() {
        return willDie;
    }
}
