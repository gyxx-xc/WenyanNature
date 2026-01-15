package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.utils.WenyanThreading;
import net.minecraft.world.entity.player.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a Wenyan program execution context.
 * Handles program compilation, thread management, and scheduling.
 */
public class WenyanProgram {
    /**
     * The base runtime environment for the program
     */
    public final WenyanRuntime baseEnvironment;

    /**
     * Counter for currently running threads
     */
    public final AtomicInteger runningThreadsNumber = new AtomicInteger(0);

    /**
     * Queue of threads ready to run
     */
    public final Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();

    /**
     * Semaphore controlling execution steps across threads
     */
    private final Semaphore accumulatedSteps = new Semaphore(0);

    /**
     * The Java thread that runs the programs (scheduler as master)
     */
    private final Thread programJavaThread = new Thread(this::scheduler);

    public static final int MAX_THREAD = 3;

    // STUB: used in error handler, might changed
    //   should not be used in new code
    @Deprecated
    public final Player holder;

    /**
     * Platform-specific integration
     */
    public final IWenyanPlatform platform;

    /**
     * Cost in steps to switch between threads
     */
    private static final int SWITCH_COST = 1;

    /**
     * Steps allocated to a thread when scheduled
     */
    private static final int SWITCH_STEP = 100;

    /**
     * Creates a new Wenyan program from the given code.
     *
     * @param holder   The player who created this program
     * @param platform The platform integration
     */
    public WenyanProgram(Player holder, IWenyanPlatform platform) {
        this.platform = platform;
        this.baseEnvironment = new WenyanRuntime(null);
        baseEnvironment.importPackage(WenyanPackages.WENYAN_BASIC_PACKAGES);
        platform.changeInitEnvironment(baseEnvironment);
        this.holder = holder;
    }

    /**
     * Creates a new thread for this program with the base environment.
     */
    @WenyanThreading(planning = true)
    public WenyanThread createThread(String code) throws WenyanException.WenyanVarException {
        if (!programJavaThread.isAlive()) {
            // DCL? what is that
            try {
                programJavaThread.start();
            } catch (IllegalThreadStateException ignored) {
            }
        }

        var thread = WenyanThread.ofCode(code, baseEnvironment, this);

        int threadsNumber = runningThreadsNumber.getAndIncrement();
        if (threadsNumber >= MAX_THREAD) {
            WenyanException.handleException(holder, "too many threads");
            runningThreadsNumber.getAndDecrement();
            throw new WenyanException.WenyanVarException("too many threads");
        }
        readyQueue.add(thread);
        return thread;
    }

    /**
     * Allocates execution steps to the program.
     *
     * @param steps Number of execution steps to allocate
     */
    public void step(int steps) {
        if (!isRunning()) {
            throw new IllegalStateException("unreached: Program is not running");
        }
        accumulatedSteps.release(steps);
    }

    /**
     * Stops the program by interrupting the scheduler thread.
     */
    public void stop() {
        if (programJavaThread.isAlive()) {
            programJavaThread.interrupt();
        }
        runningThreadsNumber.set(0);
    }

    /**
     * Unblocks a Wenyan thread, moving it to the ready queue.
     *
     * @param wenyanThread The thread to unblock
     * @throws RuntimeException If the thread is not in a blocked state
     */
    public static void unblock(WenyanThread wenyanThread) {
        if (wenyanThread.state == WenyanThread.State.BLOCKED) {
            wenyanThread.state = WenyanThread.State.READY;
            wenyanThread.program.readyQueue.add(wenyanThread);
        } else {
            throw new RuntimeException("unreached");
        }
    }

    /**
     * Checks if the program has any running threads.
     *
     * @return True if the program has running threads, false otherwise
     */
    public boolean isRunning() {
        return runningThreadsNumber.get() > 0;
    }

    /**
     * The scheduler loop that runs on a separate Java thread.
     * Manages thread execution and switching.
     */
    @WenyanThreading
    public void scheduler() {
        try {
            // not stop until interrupted (object unloaded)
            // noinspection InfiniteLoopStatement
            while (true) {
                if (accumulatedSteps.availablePermits() > 10000) {
                    WenyanException.handleException(holder, "program running too slow");
                    readyQueue.clear();
                    runningThreadsNumber.set(0);
                    accumulatedSteps.drainPermits();
                    throw new InterruptedException("killed");
                }

                accumulatedSteps.acquire(SWITCH_COST);
                if (readyQueue.isEmpty()) {
                    // TODO: make it not busy wait
                    accumulatedSteps.drainPermits();
                    continue;
                }

                WenyanThread thread = readyQueue.poll();
                thread.assignedSteps += SWITCH_STEP;
                thread.programLoop(accumulatedSteps);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
