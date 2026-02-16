package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;

public interface IThreadHolder<T extends IWenyanThread> {
    void run(int step);
    void pause(); // for switch

    void setThread(T thread);
    T getThread();

    // Warpper functions for convenience
    default IWenyanProgram program() {
        return getThread().getProgram();
    }

    default IWenyanPlatform platform() {
        return program().getPlatform();
    }

    default void block() throws WenyanException {
        program().block(this);
    }

    default void unblock() throws WenyanException {
        program().unblock(this);
    }

    default void yield() throws WenyanException {
        program().yield(this);
    }

    default void die() throws WenyanException {
        program().die(this);
    }

    default void consumeStep(int step) throws WenyanException {
        program().consumeStep(this, step);
    }
}
