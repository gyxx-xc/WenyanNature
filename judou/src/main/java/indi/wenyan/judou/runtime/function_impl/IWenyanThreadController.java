package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.runtime.IWenyanScheduler;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;

public interface IWenyanThreadController<T extends IWenyanScheduler.IWenyanThread> {
    void unblock(IThreadHolder<T> runner) throws WenyanUnreachedException;

    // NOTE: not intend to call anywhere outside run(steps)
    void block(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void yield(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void die(IThreadHolder<T> runner) throws WenyanUnreachedException;
}
