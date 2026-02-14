package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.LoggerManager;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WenyanProgramImpl implements IWenyanProgram<WenyanProgramImpl.PCB> {
    public static final int SLICE_STEP = 1000;
    public static final int MAX_THREAD = 10;

    private final Lock stepLock = new ReentrantLock();
    private final Condition newStepsCondition = stepLock.newCondition();
    /**
     * Semaphore controlling execution steps across threads
     */
    private int accumulatedSteps = 0;
    /**
     * used for given warning and give status to platform only,
     * will monitor if there's idle (empty thread in pool or has thread but blocked)
     * and remain the value until next step() is called
     **/
    @Getter
    private boolean idleFlag = false;

    // NOTE: all thread = current running thread + ready queue threads + blocked threads (hold by ExecQueue)
    public final Collection<PCB> allThreads = ConcurrentHashMap.newKeySet(MAX_THREAD);

    private final Cleaner.Cleanable executorCleanable;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    /**
     * Platform-specific integration
     */
    @Getter
    public final IWenyanPlatform platform;

    private static final ScheduledExecutorService WATCHDOG = Executors.newSingleThreadScheduledExecutor(r -> {
        var thread = new Thread(r, "WenyanProgramImpl-watchdog");
        thread.setDaemon(true);
        return thread;
    });

    public WenyanProgramImpl(IWenyanPlatform platform) {
        this.platform = platform;
        executorCleanable = Cleaner.create().register(this, () -> {
            if (!executor.isShutdown())
                executor.shutdownNow();
        });
    }

    @Override
    public void step(int steps) {
        if (executor.isShutdown()) return;
        if (steps <= 0) {
            throw new IllegalArgumentException("steps must be positive");
        }
        stepLock.lock();
        try {
            if (accumulatedSteps > 0) {
                // STUB: if no idle
                if (!idleFlag)
                    LoggerManager.getLogger().warn(
                            "program running too slow, step {} but {} accumulated",
                            steps, accumulatedSteps); // make > 0 for less confused message
            }
            accumulatedSteps = steps;
            newStepsCondition.signalAll(); // should be only one wait
        } finally {
            stepLock.unlock();
        }
        // update idle (i.e. case in update idle, consume step)
        // ignore case in consume step, no possible since we just signal it
        // connot use updateIdle() since thread not dying ot blocking

        // use getActiveCount thought there's rare case that active thread is ending
        // but that doesn't matter, since this only used for Visual effects
        idleFlag = executor.getActiveCount() == 0; // && queue.isEmpty (always true is first true)
    }

    @Override
    public boolean isRunning() {
        return !allThreads.isEmpty();
    }

    @Override
    public void block(IThreadHolder<PCB> runner) throws WenyanException.WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == WenyanThread.State.READY) {
            thread.setState(WenyanThread.State.BLOCKED);
            updateIdle();
        } else {
            throw new WenyanException.WenyanUnreachedException();
        }
    }

    @Override
    public void unblock(IThreadHolder<PCB> runner) throws WenyanException.WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == WenyanThread.State.BLOCKED) {
            thread.setState(WenyanThread.State.READY);
            submitThread(runner);
        } else {
            throw new WenyanException.WenyanUnreachedException();
        }
    }

    @Override
    public void yield(IThreadHolder<PCB> runner) throws WenyanException.WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == WenyanThread.State.READY) {
            submitThread(runner);
        } else {
            throw new WenyanException.WenyanUnreachedException();
        }
    }

    @Override
    public void die(IThreadHolder<PCB> runner) throws WenyanException.WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == WenyanThread.State.DYING)
            throw new WenyanException.WenyanUnreachedException();
        allThreads.remove(thread);
        thread.setState(WenyanThread.State.DYING);
        updateIdle();
    }

    @Override
    public void stop() {
        allThreads.forEach(thread -> {
            thread.getWatchdog().cancel(false);
        });
        executorCleanable.clean();
        allThreads.clear();
    }

    @Override
    public <C extends IThreadHolder<PCB> & IBytecodeRunner> void create(C runner) throws WenyanException {
        if (allThreads.size() + 1 > MAX_THREAD) {
            throw new WenyanException.WenyanVarException("too many threads");
        }

        var thread = new PCB(runner, this);
        runner.setThread(thread);
        allThreads.add(thread);
        // after add, size = size + 1 if no other thread is creating
        // else size larger than MAX_THREAD. not allowed, so remove
        if (allThreads.size() > MAX_THREAD) {
            allThreads.remove(thread);
            throw new WenyanException.WenyanVarException("too many threads");
        } else {
            unblock(runner);
        }
    }

    @Override
    public void consumeStep(IThreadHolder<PCB> runner, int i) {
        stepLock.lock();
        try {
            if (accumulatedSteps < i) {
                runner.getThread().getWatchdog().cancel(false);
                idleFlag = true;
                newStepsCondition.await();
            }
            accumulatedSteps -= i;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            stepLock.unlock();
        }
    }

    /**
     * only intend to be called when thread is ended (i.e. die, block)
     */
    private void updateIdle() {
        if (executor.getQueue().isEmpty()) idleFlag = true;
    }

    private void submitThread(@NotNull IThreadHolder<PCB> runner) {
        var thread = runner.getThread();
        executor.submit(() -> {
            try {
                AtomicBoolean done = new AtomicBoolean(false);
                var f = WATCHDOG.schedule(() -> {
                    try {
                        if (!done.get()) {
                            LoggerManager.getLogger().error("program running too slow for given step, program stop");
                            LoggerManager.getLogger().debug("program: {}", thread.getRunner());
                            platform.handleError("program running too slow");
                            stop();
                        }
                    } catch (Exception e) {
                        LoggerManager.getLogger().error("unexcepted: Watchdog failed", e);
                    }
                }, 5, TimeUnit.MILLISECONDS);
                thread.setWatchdog(f);

                try {
                    thread.getRunner().run(SLICE_STEP);
                } finally {
                    f.cancel(true);
                    done.set(true);
                }
            } catch (Exception e) {
                LoggerManager.getLogger().error("unexcepted: thread failed", e);
                stop();
            }
        });
    }

    @Data
    public static class PCB implements IWenyanThread { // i.e. PCB
        final IBytecodeRunner runner;
        WenyanThread.State state = WenyanThread.State.BLOCKED;
        final IWenyanProgram program;
        ScheduledFuture<?> watchdog;

        public PCB(IBytecodeRunner runner, IWenyanProgram program) {
            this.runner = runner;
            this.program = program;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof PCB)) return false;

            PCB pcb = (PCB) o;
            return Objects.equals(getRunner(), pcb.getRunner());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getRunner());
        }
    }
}
