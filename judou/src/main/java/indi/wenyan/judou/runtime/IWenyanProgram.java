package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface IWenyanProgram<T extends IWenyanThread> {
    boolean isAvailable();

    /**
     * Allocates execution steps to the program.
     * Not Thread-safe, should be only called from minecraft thread.
     */
    void step();

    IWenyanPlatform getPlatform();

    boolean isRunning();

    void unblock(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void stop();

    void create(IThreadHolder<T> runner) throws WenyanException;

    // NOTE: not intend to call anywhere outside run(steps)
    void block(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void yield(IThreadHolder<T> runner) throws WenyanUnreachedException;

    void die(IThreadHolder<T> runner) throws WenyanUnreachedException;
}
