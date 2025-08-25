package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.compiler.visitor.WenyanVisitor;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.IWenyanPlatform;
import indi.wenyan.interpreter.utils.WenyanPackages;
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
    /** The source code of the program */
    public final String code;

    /** The base bytecode compiled from the source code */
    public final WenyanBytecode baseBytecode = new WenyanBytecode();

    /** The base runtime environment for the program */
    public final WenyanRuntime baseEnvironment;

    /** Counter for currently running threads */
    public AtomicInteger runningCounter = new AtomicInteger(0);

    /** Queue of threads ready to run */
    public final Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();

    /** Semaphore controlling execution steps across threads */
    private final Semaphore accumulatedSteps = new Semaphore(0);

    /** The Java thread that runs the scheduler */
    private final Thread programJavaThread;

    // STUB: used in error handler, might changed
    public final Player holder;

    /** Platform-specific integration */
    public final IWenyanPlatform platform;

    /** Cost in steps to switch between threads */
    private static final int SWITCH_COST = 5;

    /** Steps allocated to a thread when scheduled */
    private static final int SWITCH_STEP = 10;

    /**
     * Creates a new Wenyan program from the given code.
     *
     * @param code The Wenyan program source code
     * @param holder The player who created this program
     * @param platform The platform integration
     */
    public WenyanProgram(String code, Player holder, IWenyanPlatform platform) {
        this.code = code;
        this.platform = platform;
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        try {
            visitor.visit(WenyanVisitor.program(code));
        } catch (WenyanException e) {
            WenyanException.handleException(holder, e.getMessage());
        }
        this.baseEnvironment = new WenyanRuntime(null);
        baseEnvironment.importEnvironment(WenyanPackages.WENYAN_BASIC_PACKAGES);
        platform.initEnvironment(baseEnvironment);
        this.holder = holder;
        programJavaThread = new Thread(this::scheduler);
        programJavaThread.start();
    }

    /**
     * Creates a new thread for this program with the base environment.
     */
    public void createThread() {
        WenyanThread thread = new WenyanThread(this);
        thread.call(baseEnvironment);
        thread.call(new WenyanRuntime(baseBytecode));
        readyQueue.add(thread);
        runningCounter.getAndIncrement();
    }

    /**
     * Allocates execution steps to the program.
     *
     * @param steps Number of execution steps to allocate
     */
    public void step(int steps) {
        accumulatedSteps.release(steps);
    }

    /**
     * Stops the program by interrupting the scheduler thread.
     */
    public void stop() {
        programJavaThread.interrupt();
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
        return runningCounter.get() > 0;
    }

    /**
     * The scheduler loop that runs on a separate Java thread.
     * Manages thread execution and switching.
     */
    // NOTE: this on other thread
    public void scheduler() {
        try {
            // not stop until interrupted (object unloaded)
            //noinspection InfiniteLoopStatement
            while (true) {
                accumulatedSteps.acquire(SWITCH_COST);
                if (readyQueue.isEmpty()) {
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
