package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.utils.LoggerManager;
import lombok.Data;

import java.lang.ref.Cleaner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
    // used for given warning and give status to platform only
    boolean isIdle = false;

    // NOTE: all thread = current running thread + ready queue threads + blocked threads (hold by ExecQueue)
    public final AtomicInteger runningThreadsNumber = new AtomicInteger(0);

    private final Cleaner.Cleanable executorCleanable;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    /**
     * Platform-specific integration
     */
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
                LoggerManager.getLogger().warn(
                        "program running too slow, step {} but {} accumulated",
                        steps, accumulatedSteps); // make > 0 for less confused message
            }
            accumulatedSteps = steps;
            newStepsCondition.signalAll(); // should be only one wait
        } finally {
            stepLock.unlock();
        }
    }

    @Override
    public boolean isRunning() {
        return runningThreadsNumber.get() > 0;
    }

    @Override
    public void block(IThreadHolder<PCB> runner) throws WenyanException.WenyanUnreachedException {
        var thread = runner.getThread();
        if (thread.getState() == WenyanThread.State.READY) {
            thread.setState(WenyanThread.State.BLOCKED);
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

    private void submitThread(IThreadHolder<PCB> runner) {
        var thread = runner.getThread();
        executor.submit(() -> {
            AtomicBoolean done = new AtomicBoolean(false);
            var f = WATCHDOG.schedule(() -> {
                try {
                    if (!done.get()) {
                        dieWithException(runner, new WenyanException("program running too slow"));
                        stop();
                    }
                } catch (Exception e) {
                    LoggerManager.getLogger().error("unexcepted: Watchdog failed", e);
                }
            }, 5, TimeUnit.MILLISECONDS);
            thread.setWatchdog(f);

            try {
                thread.getRunner().run(SLICE_STEP);
            } catch (WenyanThrowException e) {
                dieWithException(runner, e);
            } finally {
                f.cancel(true);
                done.set(true);
            }
        });
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
        runningThreadsNumber.decrementAndGet();
        thread.setState(WenyanThread.State.DYING);
    }

    @Override
    public void dieWithException(IThreadHolder<PCB> runner, Exception e) {

    }

    @Override
    public void stop() {
        WATCHDOG.shutdownNow();
        executorCleanable.clean();
        runningThreadsNumber.set(0);
    }

    @Override
    public <C extends IThreadHolder<PCB> & IBytecodeRunner> void create(C runner) throws WenyanException {
        runner.setThread(new PCB(runner, this));
        int threadsNumber = runningThreadsNumber.getAndIncrement();
        if (threadsNumber >= MAX_THREAD) {
            runningThreadsNumber.getAndDecrement();
            throw new WenyanException.WenyanVarException("too many threads");
        } // else
        submitThread(runner);
    }

    @Override
    public void consumeStep(IThreadHolder<PCB> runner, int i) {
        stepLock.lock();
        try {
            if (accumulatedSteps < i) {
                runner.getThread().getWatchdog().cancel(false);
                newStepsCondition.await();
            }
            accumulatedSteps -= i;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            stepLock.unlock();
        }
    }

    @Data
    public static class PCB { // i.e. PCB
        final IBytecodeRunner runner;
        WenyanThread.State state = WenyanThread.State.BLOCKED;
        final IWenyanProgram program;
        ScheduledFuture<?> watchdog;

        public PCB(IBytecodeRunner runner, IWenyanProgram program) {
            this.runner = runner;
            this.program = program;
        }
    }
}
