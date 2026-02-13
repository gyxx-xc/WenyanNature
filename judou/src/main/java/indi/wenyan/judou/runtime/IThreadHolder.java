package indi.wenyan.judou.runtime;

public interface IThreadHolder<T> {
    void setThread(T thread);
    T getThread();
}
