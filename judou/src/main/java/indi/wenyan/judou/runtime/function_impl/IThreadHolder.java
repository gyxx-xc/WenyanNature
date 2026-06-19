package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.runtime.IRunner;
import indi.wenyan.judou.api.runtime.IWenyanScheduler;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;

/// Runner interface expose to scheduler
public interface IThreadHolder<T extends IWenyanScheduler.IWenyanThread> extends IRunner {
    void setThread(T thread);

    T getThread();

    /// run for given step and return the actual step run
    int run(int step);

    void pause(); // for switch

    // Wrapper functions for convenience
    default IWenyanScheduler<T> program() {
        return getThread().getProgram();
    }

    @Override
    default IWenyanPlatform platform() {
        return program().getPlatform();
    }

    @Override
    default void block() throws WenyanUnreachedException {
        program().block(this);
    }

    @Override
    default void unblock() throws WenyanUnreachedException {
        program().unblock(this);
    }

    @Override
    default void yield() throws WenyanUnreachedException {
        program().yield(this);
    }

    @Override
    default void die() throws WenyanUnreachedException {
        program().die(this);
    }

    @Override
    default <A extends IWenyanScheduler.IWenyanThread> void create(IThreadHolder<A> newThread) throws WenyanException {
        // NOTE: need check when refactor.
        //noinspection unchecked
        program().create((IThreadHolder<T>) newThread);
    }
}
