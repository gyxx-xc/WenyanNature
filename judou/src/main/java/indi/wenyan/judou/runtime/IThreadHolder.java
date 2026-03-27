package indi.wenyan.judou.runtime;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanUnreachedException;

public interface IThreadHolder<T extends IWenyanThread> {
    /// run for given step and return the actual step runned
    int run(int step);
    void pause(); // for switch

    void setThread(T thread);
    T getThread();

    // Warpper functions for convenience
    default IWenyanProgram<T> program() {
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
}
