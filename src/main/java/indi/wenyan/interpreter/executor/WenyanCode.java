package indi.wenyan.interpreter.executor;

public abstract class WenyanCode {
    public final String name;

    protected WenyanCode(String name) {
        this.name = name;
    }

    public abstract void exec(int args);

    @Override
    public String toString() {
        return name;
    }
}
