package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.IThreadHolder;
import indi.wenyan.judou.runtime.function_impl.WenyanSchedularImpl;

// TODO: not use template here
public interface IWenyanScheduler<T extends IWenyanScheduler.IWenyanThread> {
    /// return false if no longer able to accepts/run thread
    boolean isAvailable();

    /**
     * Allocates execution steps to the program.
     * Not Thread-safe, should be only called from minecraft thread.
     */
    void step();

    IWenyanPlatform getPlatform();

    boolean isRunning();

    void stop();

    void create(IThreadHolder<T> runner) throws WenyanException;

    void unblock(IThreadHolder<T> runner) throws WenyanUnreachedException;

    // NOTE: not intend to call anywhere outside run(steps)
    void block(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void yield(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void die(IThreadHolder<T> runner) throws WenyanUnreachedException;

    @SuppressWarnings("unchecked")
    static <T extends IWenyanScheduler.IWenyanThread> IWenyanScheduler<T> defaultImpl(IWenyanPlatform platform, int step) {
        return (IWenyanScheduler<T>) new WenyanSchedularImpl(platform, step);
    }

    interface IWenyanThread {
        <T extends IWenyanThread> IWenyanScheduler<T> getProgram();
    }
}
