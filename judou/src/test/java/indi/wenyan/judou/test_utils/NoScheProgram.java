package indi.wenyan.judou.test_utils;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanProgram;
import indi.wenyan.judou.runtime.IWenyanThread;
import lombok.Getter;

public class NoScheProgram implements IWenyanProgram<NoScheProgram.SimpleThread> {

    @Getter
    private final IWenyanPlatform platform = new TestPlatform();

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void step() {}

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }

    @Override
    public void unblock(IThreadHolder<SimpleThread> runner) {

    }


    @Override
    public void create(IThreadHolder<SimpleThread> runner) {

    }

    @Override
    public void block(IThreadHolder<SimpleThread> runner) {

    }

    @Override
    public void yield(IThreadHolder<SimpleThread> runner) {

    }

    @Override
    public void die(IThreadHolder<SimpleThread> runner) {

    }

    public SimpleThread getThread() {
        return new SimpleThread();
    }

    public class SimpleThread implements IWenyanThread {
        @Override
        public <T extends IWenyanThread> IWenyanProgram<T> getProgram() {
            //noinspection unchecked
            return (IWenyanProgram<T>) NoScheProgram.this;
        }
    }
}
