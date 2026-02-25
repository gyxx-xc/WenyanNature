package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanUnreachedException;

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

    default void block() throws WenyanUnreachedException {
        program().block(this);
    }

    default void unblock() throws WenyanUnreachedException {
        program().unblock(this);
    }

    default void yield() throws WenyanUnreachedException {
        program().yield(this);
    }

    default void die() throws WenyanUnreachedException {
        program().die(this);
    }

    default void consumeStep(int step) throws WenyanUnreachedException {
        program().consumeStep(this, step);
    }
}
