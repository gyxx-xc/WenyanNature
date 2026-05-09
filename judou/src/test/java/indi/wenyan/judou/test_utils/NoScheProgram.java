package indi.wenyan.judou.test_utils;

import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.runtime.IThreadHolder;
import indi.wenyan.judou.api.runtime.IWenyanScheduler;
import lombok.Getter;

public class NoScheProgram implements IWenyanScheduler<NoScheProgram.SimpleThread> {

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
        public <T extends IWenyanThread> IWenyanScheduler<T> getProgram() {
            //noinspection unchecked
            return (IWenyanScheduler<T>) NoScheProgram.this;
        }
    }
}
