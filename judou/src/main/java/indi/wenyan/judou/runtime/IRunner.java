package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface IRunner {
    IWenyanPlatform platform();

    void block() throws WenyanUnreachedException;

    void unblock() throws WenyanUnreachedException;

    void yield() throws WenyanUnreachedException;

    void die() throws WenyanUnreachedException;

    <T extends IWenyanThread> void create(IThreadHolder<T> newThread) throws WenyanException;
}
