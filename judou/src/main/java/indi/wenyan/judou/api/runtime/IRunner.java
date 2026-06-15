package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.IThreadHolder;

public interface IRunner {
    IWenyanPlatform platform();

    void block() throws WenyanUnreachedException;

    void unblock() throws WenyanUnreachedException;

    void yield() throws WenyanUnreachedException;

    void die() throws WenyanUnreachedException;

    <T extends IWenyanScheduler.IWenyanThread> void create(IThreadHolder<T> newThread) throws WenyanException;
}
