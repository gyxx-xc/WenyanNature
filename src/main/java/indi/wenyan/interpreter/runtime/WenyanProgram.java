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

public class WenyanProgram {
    public final String code;

    public final WenyanBytecode baseBytecode = new WenyanBytecode();
    public final WenyanRuntime baseEnvironment;

    public AtomicInteger runningCounter = new AtomicInteger(0);
    public final Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();
    private final Semaphore accumulatedSteps = new Semaphore(0);

    private final Thread programJavaThread;

    // STUB: used in error handler, might changed
    public final Player holder;

    public final IWenyanPlatform platform;

    private static final int SWITCH_COST = 5;
    private static final int SWITCH_STEP = 10;

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

    public void createThread() {
        WenyanThread thread = new WenyanThread(this);
        thread.call(baseEnvironment);
        thread.call(new WenyanRuntime(baseBytecode));
        readyQueue.add(thread);
        runningCounter.getAndIncrement();
    }

    public void step(int steps) {
        accumulatedSteps.release(steps);
    }

    public void stop() {
        programJavaThread.interrupt();
    }

    public static void unblock(WenyanThread wenyanThread) {
        if (wenyanThread.state == WenyanThread.State.BLOCKED) {
            wenyanThread.state = WenyanThread.State.READY;
            wenyanThread.program.readyQueue.add(wenyanThread);
        } else {
            throw new RuntimeException("unreached");
        }
    }

    public boolean isRunning() {
        return runningCounter.get() > 0;
    }

    // this on other thread
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
