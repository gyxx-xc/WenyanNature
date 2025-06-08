package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanThread;

public abstract class WenyanCode {
    public final String name;

    protected WenyanCode(String name) {
        this.name = name;
    }

    public abstract void exec(int args, WenyanThread thread);

    public int getStep(int args, WenyanThread thread) {
        return 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
