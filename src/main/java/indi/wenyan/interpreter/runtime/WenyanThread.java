package indi.wenyan.interpreter.runtime;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.compiler.WenyanVerifier;
import indi.wenyan.interpreter.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.compiler.visitor.WenyanVisitor;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.utils.WenyanCodes;
import indi.wenyan.interpreter.utils.WenyanThreading;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;
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
    public final String code;

    /**
     * Stack of runtime environments
     */
    public final Stack<WenyanRuntime> runtimes = new Stack<>();

    @Getter
    @Setter
    private WenyanRuntime mainRuntime = null;

    /**
     * Number of steps allocated to this thread
     */
    public int assignedSteps = 0;

    /**
     * The program this thread belongs to
     */
    public final WenyanProgram program;

    /**
     * Current execution state
     */
    public State state = State.READY;

    /**
     * Possible states of a Wenyan thread
     */
    public enum State {
        READY,
        BLOCKED,
        DYING
    }

    public static @NotNull WenyanThread ofCode(String code, WenyanRuntime baseEnvironment, WenyanProgram program) {
        WenyanThread thread = new WenyanThread(code, program);
        thread.call(baseEnvironment);

        var bytecode = new WenyanBytecode();
        WenyanCompilerEnvironment environment = new WenyanCompilerEnvironment(bytecode);
        WenyanVisitor visitor = new WenyanMainVisitor(environment);
        visitor.visit(WenyanVisitor.program(code));
        environment.enterContext(0, 0, 0, 0);
        environment.add(WenyanCodes.PUSH, WenyanNull.NULL);
        environment.add(WenyanCodes.RET);
        environment.exitContext();
        try {
            WenyanVerifier.verify(bytecode);
        } catch (WenyanThrowException e) {
            program.platform.handleError(e.getMessage());
        }

        // call main
        WenyanRuntime mainRuntime = new WenyanRuntime(bytecode);
        thread.call(mainRuntime);
        thread.setMainRuntime(mainRuntime);
        return thread;
    }

    /**
     * Creates a new thread belonging to the given program.
     *
     * @param program The program this thread belongs to
     */
    public WenyanThread(String code, WenyanProgram program) {
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

                if (runtime.programCounter < 0 || runtime.programCounter >= runtime.bytecode.size()) {
                    dieWithException(new WenyanException.WenyanUnreachedException());
                    return;
                }

                WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);
                int needStep = code.code().getStep(code.arg(), this);

                if (assignedSteps < needStep) {
                    this.yield();
                    return; //switch
                }
                assignedSteps -= needStep;
                accumulatedSteps.acquire(needStep);

                code.code().exec(code.arg(), this);
                if (!runtime.PCFlag)
                    runtime.programCounter++;
                runtime.PCFlag = false;

                if (state != State.READY) {
                    return; // yield
                }
            } catch (Exception e) {
                dieWithException(e);
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
            if (e instanceof WenyanException) {
                WenyanBytecode.Context context = currentRuntime().bytecode.getContext(currentRuntime().programCounter);
//                WenyanException.handleException(program.holder, context.line() + ":" + context.column() + " " +
//                        code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                program.platform.handleError(context.line() + ":" + context.column() + " " +
                        code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
            } else if (e instanceof WenyanThrowException) {
                WenyanBytecode.Context context =
                        currentRuntime().bytecode.getContext(currentRuntime().programCounter - 1);
//                WenyanException.handleException(program.holder, context.line() + ":" + context.column() + " " +
//                        code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
                program.platform.handleError(context.line() + ":" + context.column() + " " +
                        code.substring(context.contentStart(), context.contentEnd()) + " " + e.getMessage());
            } else {
                // for debug only
                WenyanBytecode.Context context =
                        currentRuntime().bytecode.getContext(currentRuntime().programCounter - 1);
                WenyanProgramming.LOGGER.debug("{}", code);
                WenyanProgramming.LOGGER.error("{}:{} {}", context.line(), context.column(), code.substring(context.contentStart(), context.contentEnd()));
                WenyanProgramming.LOGGER.error("WenyanThread died with an unexpected exception", e);
                WenyanProgramming.LOGGER.error(e.getMessage());
//                WenyanException.handleException(program.holder, "WenyanThread died with an unexpected exception, killed");
                program.platform.handleError("WenyanThread died with an unexpected exception, killed");
            }
        } catch (Exception unexpected) {
            WenyanProgramming.LOGGER.error("during handling an exception", e);
            WenyanProgramming.LOGGER.error("WenyanThread died with an unexpected exception", unexpected);
//            WenyanException.handleException(program.holder, "WenyanThread died with an unexpected exception, killed");
            program.platform.handleError("WenyanThread died with an unexpected exception, killed");
        }
        try {
            die();
        } catch (WenyanException.WenyanUnreachedException ex) {
            WenyanProgramming.LOGGER.error("during handling an exception", e);
            WenyanProgramming.LOGGER.error("WenyanThread died with an unexpected exception", ex);
//            WenyanException.handleException(program.holder, "WenyanThread died with an unexpected exception, killed");
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
     * Yields execution by adding this thread back to the ready queue.
     */
    public void yield() {
        if (state == State.READY) {
            program.readyQueue.add(this);
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
     * Gets the current runtime environment from the top of the stack.
     *
     * @return The current runtime environment
     */
    public WenyanRuntime currentRuntime() {
        return runtimes.peek();
    }

    /**
     * Removes the top runtime environment from the stack.
     */
    public void ret() {
        var runtime = runtimes.pop();
        runtime.finishFlag = true;
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
        for (int i = runtimes.size() - 1; i >= 0; i--) {
            if (runtimes.get(i).variables.containsKey(id)) {
                value = runtimes.get(i).variables.get(id);
                break;
            }
        }
        if (value == null)
            throw new WenyanException(Component.translatable("error.wenyan_programming.variable_not_found_").getString() + id);
        return value;
    }
}
