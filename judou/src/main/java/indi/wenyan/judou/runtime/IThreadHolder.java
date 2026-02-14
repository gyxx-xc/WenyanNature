package indi.wenyan.judou.runtime;

public interface IThreadHolder<T extends IWenyanThread> {
    void setThread(T thread);
    T getThread();
    default IWenyanProgram program() {
        return getThread().getProgram();
    }
}
