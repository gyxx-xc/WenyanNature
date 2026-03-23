package indi.wenyan.judou.runtime;

public interface IWenyanThread {
    <T extends IWenyanThread> IWenyanProgram<T> getProgram();
}
