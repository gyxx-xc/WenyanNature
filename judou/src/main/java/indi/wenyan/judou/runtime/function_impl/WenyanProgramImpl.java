package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.IWenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.ConfigManager;
import indi.wenyan.judou.utils.LoggerManager;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.*;

public class WenyanProgramImpl implements IWenyanProgram<WenyanProgramImpl.PCB> {
    private final int sliceStep = ConfigManager.getConfig().getMaxSlice();
    private final int maxThread = ConfigManager.getConfig().getMaxThread();
    private final int watchdogTimeout = ConfigManager.getConfig().getWatchdogTimeout();

    /**
     * Semaphore controlling execution steps across threads.
     * Should be only read in wenyan thread.
     */
    private volatile int accumulatedSteps = 0;
    private final Semaphore stepLock = new Semaphore(0);
    private final int step;

    /**
     * used for given warning and give status to platform only, NOT ACCURATE.
     * will monitor if there's idle (empty thread in pool or has thread but blocked)
     * and remain the value until next step() is called
     **/
    @Getter
    private boolean isIdle = false;

    // NOTE: all thread = current running thread + ready queue threads + blocked threads (hold by ExecQueue)
    public final Collection<PCB> allThreads = ConcurrentHashMap.newKeySet(maxThread);

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    /**
     * Platform-specific integration
     */
    @Getter
    public final IWenyanPlatform platform;

    private static final ScheduledExecutorService WATCHDOG = Executors.newSingleThreadScheduledExecutor(r -> {
        var thread = new Thread(r, "WenyanProgram-watchdog");
        thread.setDaemon(true);
        return thread;
    });

    public WenyanProgramImpl(IWenyanPlatform platform, int step) {
        this.platform = platform;
        this.step = step;
    }

    @Override
    public void step() {
        if (executor.isShutdown()) return;
        if (accumulatedSteps > 0) {
            if (!isIdle)
                LoggerManager.getLogger().warn(
                        "program running too slow, step {} but {} accumulated",
                        step, accumulatedSteps);
        }
        if (stepLock.availablePermits() == 0)
            stepLock.release(); // is fine if permits > 0 here
    }

    @Override
    public boolean isRunning() {
        return !allThreads.isEmpty();
    }

    @Override
    public void block(IThreadHolder<PCB> runner) throws WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == State.READY && allThreads.contains(thread)) {
            thread.setState(State.BLOCKED);
            updateIdle();
            runner.pause();
        } else {
            throw new WenyanUnreachedException();
        }
    }

    @Override
    public void unblock(IThreadHolder<PCB> runner) throws WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == State.BLOCKED && allThreads.contains(thread)) {
            thread.setState(State.READY);
            submitThread(runner);
        } else {
            throw new WenyanUnreachedException();
        }
    }

    @Override
    public void yield(IThreadHolder<PCB> runner) throws WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == State.READY && allThreads.contains(thread)) {
            submitThread(runner);
            runner.pause();
        } else {
            throw new WenyanUnreachedException();
        }
    }

    @Override
    public void die(IThreadHolder<PCB> runner) throws WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == State.DYING || !allThreads.contains(thread))
            throw new WenyanUnreachedException();
        allThreads.remove(thread);
        thread.setState(State.DYING);
        runner.pause();
        updateIdle();
    }

    @Getter
    private boolean available = true;

    @Override
    public void stop() {
        allThreads.forEach(thread -> {
            thread.getRunner().pause();
            if (thread.getWatchdog() != null)
                thread.getWatchdog().cancel(false);
        });
        executor.shutdownNow();
        allThreads.clear();
        available = false;
    }

    @Override
    public void create(IThreadHolder<PCB> runner) throws WenyanException {
        if (allThreads.size() + 1 > maxThread) {
            throw new WenyanException.WenyanVarException(JudouExceptionText.TooManyThreads.string());
        }

        var thread = new PCB(runner, this);
        runner.setThread(thread);
        allThreads.add(thread);
        // after add, size = size + 1 if no other thread is creating
        // else size larger than MAX_THREAD. not allowed, so remove
        if (allThreads.size() > maxThread) {
            allThreads.remove(thread);
            throw new WenyanException.WenyanVarException(JudouExceptionText.TooManyThreads.string());
        } else {
            unblock(runner);
        }
    }

    /**
     * only intend to be called when thread is ended (i.e. die, block)
     */
    private void updateIdle() {
        if (executor.getQueue().isEmpty()) isIdle = true;
    }

    private void submitThread(@NotNull IThreadHolder<PCB> runner) {
        var thread = runner.getThread();
        executor.execute(() -> {
            try {
                if (accumulatedSteps <= 0) {
                    isIdle = true; // although we might encounter non-blocking acquire here, never mind
                    stepLock.acquire();
                    stepLock.drainPermits(); // if permits > 1
                    accumulatedSteps = step;
                    isIdle = false;
                } else {
                    if (stepLock.tryAcquire()) {
                        stepLock.drainPermits();
                        accumulatedSteps = step;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            startWatchdog(thread);
            try {
                // NOTE: only change the steps in this single thread, so no need to use atomic
                //noinspection NonAtomicOperationOnVolatileField
                accumulatedSteps -= thread.getRunner().run(Math.min(sliceStep, accumulatedSteps));
            } finally {
                thread.getWatchdog().cancel(false);
            }
        });
    }

    private void startWatchdog(PCB thread) {
        var f = WATCHDOG.schedule(() -> {
            try {
                Logger logger = LoggerManager.getLogger();
                logger.error("program running too slow for given step, program stop");
                logger.debug("program: {}", thread.getRunner());
            } catch (IllegalStateException ignore) {
                // unreached. however, if so, program still need stop
            }
            stop(); // stop first, in case handleError throw unexpected error
            platform.handleError(JudouExceptionText.RunningTooSlow.string());
        }, watchdogTimeout, TimeUnit.MILLISECONDS);
        thread.setWatchdog(f);
    }

    @Data
    public static class PCB implements IWenyanThread { // i.e. PCB
        final IThreadHolder<PCB> runner;
        final IWenyanProgram<PCB> program;
        State state = State.BLOCKED;
        ScheduledFuture<?> watchdog;

        public PCB(IThreadHolder<PCB> runner, IWenyanProgram<PCB> program) {
            this.runner = runner;
            this.program = program;
        }

        @Override
        public final boolean equals(Object o) {
            // if same thread -> same instance
            return this == o;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    enum State {
        READY, BLOCKED, DYING
    }
}
