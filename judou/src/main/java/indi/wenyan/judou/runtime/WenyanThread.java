package indi.wenyan.judou.runtime;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.compiler.WenyanCompilerEnvironment;
import indi.wenyan.judou.compiler.WenyanVerifier;
import indi.wenyan.judou.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.judou.compiler.visitor.WenyanVisitor;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanParseTreeException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.LanguageManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.WenyanCodes;
import indi.wenyan.judou.utils.WenyanThreading;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;

/**
 * Represents a thread of execution in a Wenyan program.
 * Manages its execution state and runtime stack.
 */
@WenyanThreading
public class WenyanThread {
    /**
     * The source code of the program
     */
    private final String code;

    /**
     * Stack of runtime environments
     */
    private final Deque<WenyanRuntime> runtimes = new ArrayDeque<>();

    @Getter
    private WenyanRuntime mainRuntime = null;

    /**
     * Number of steps allocated to this thread
     */
    private int assignedSteps = 0;

    /**
     * The program this thread belongs to
     */
    public final WenyanProgram program;

    /**
     * Current execution state
     */
    @Getter
    private State state = State.BLOCKED;

    /**
     * Possible states of a Wenyan thread
     */
    public enum State {
        READY,
        BLOCKED,
        DYING
    }

    public static @NotNull WenyanThread ofCode(String code, WenyanRuntime baseEnvironment, WenyanProgram program) throws WenyanThrowException {
        WenyanThread thread = new WenyanThread(code, program);
        thread.call(baseEnvironment);

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

    /**
     * Creates a new thread belonging to the given program.
     *
     * @param program The program this thread belongs to
     */
    private WenyanThread(String code, WenyanProgram program) {
        this.program = program;
        this.code = code;
    }

    /**
     * Executes the program loop until interrupted, blocked, or steps are exhausted.
     * Should only be called by the scheduler thread.
     *
     * @param accumulatedSteps Semaphore for managing execution steps
     * @throws InterruptedException If the thread is interrupted
     */
    // NOTE: This method should be only called by the main thread to run the program loop.
    //  since it needs scheduling after return
    public void programLoop(Semaphore accumulatedSteps) throws InterruptedException {
        while (true) {
            try {
                if (mainRuntime.finishFlag) {
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

                if (assignedSteps < needStep) {
                    this.yieldThread();
                    return; //switch
                }
                assignedSteps -= needStep;
                accumulatedSteps.acquire(needStep);

                bytecode.code().getCode().exec(bytecode.arg(), this);
                if (!runtime.PCFlag)
                    runtime.programCounter++;
                runtime.PCFlag = false;

                if (state != State.READY) {
                    return; // yield
                }
            } catch (Exception e) {
                dieWithException(e);
                // rethrow interrupt
                if (e instanceof InterruptedException ie)
                    throw ie;
                return;
            }
        }
    }

    /**
     * Terminates the thread with an exception, displaying appropriate error messages.
     *
     * @param e The exception that caused the thread to die
     */
    public void dieWithException(Exception e) {
        try {
            var runtime = currentRuntime();
            if (runtime.getBytecode() == null) {
                LoggerManager.getLogger().error("during handling an exception", e);
                program.platform.handleError("WenyanThread died with an unexpected exception, killed");
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
                    program.platform.handleError("WenyanThread died with an unexpected exception, killed");
                }
                case WenyanException ignored -> {
                    WenyanBytecode.Context context = runtime.getBytecode().getContext(runtime.programCounter);
                    program.platform.handleError(context.line() + ":" + context.column() + " " +
                            code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                }
                case WenyanThrowException ignored -> {
                    WenyanBytecode.Context context =
                            runtime.getBytecode().getContext(runtime.programCounter - 1);
                    program.platform.handleError(context.line() + ":" + context.column() + " " +
                            code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                }
                case null, default ->
                        throw new WenyanException.WenyanUnreachedException(); // catch by outside
            }
        } catch (Exception unexpected) {
            LoggerManager.getLogger().error("during handling an exception", e);
            LoggerManager.getLogger().error("WenyanThread died with an unexpected exception", unexpected);
            program.platform.handleError("WenyanThread died with an unexpected exception, killed");
        }
        try {
            die();
        } catch (WenyanException.WenyanUnreachedException ex) {
            LoggerManager.getLogger().error("during handling an exception", e);
            LoggerManager.getLogger().error("WenyanThread died with an unexpected exception", ex);
            program.platform.handleError("WenyanThread died with an unexpected exception, killed");
        }
    }

    /**
     * Blocks this thread, removing it from the ready queue.
     *
     * @throws RuntimeException If the thread is not in the READY state
     */
    public void block() throws WenyanException.WenyanUnreachedException {
        if (state == State.READY) {
            state = State.BLOCKED;
            assignedSteps = 0;
        } else {
            throw new WenyanException.WenyanUnreachedException();
        }
    }

    /**
     * Unblocks a Wenyan thread, moving it to the ready queue.
     *
     * @throws RuntimeException If the thread is not in a blocked state
     */
    public void unblock() throws WenyanException.WenyanUnreachedException {
        if (state == State.BLOCKED) {
            state = State.READY;
            program.readyQueue.add(this);
        } else {
            throw new WenyanException.WenyanUnreachedException();
        }
    }

    /**
     * Yields execution by adding this thread back to the ready queue.
     */
    public void yieldThread() {
        if (state == State.READY) {
            program.readyQueue.add(this);
            assignedSteps = 0;
        }
    }

    /**
     * Terminates this thread and decrements the running counter.
     *
     * @throws RuntimeException If the thread is already dying
     */
    public void die() throws WenyanException.WenyanUnreachedException {
        if (state == State.DYING)
            throw new WenyanException.WenyanUnreachedException();
        program.runningThreadsNumber.decrementAndGet();
        state = State.DYING;
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

    public void addAssignedSteps(int steps) {
        assignedSteps += steps;
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
}
