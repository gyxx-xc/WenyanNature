package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanRuntime;

public abstract class WenyanCode {
    public final String name;

    protected WenyanCode(String name) {
        this.name = name;
    }

    public abstract void exec(int args, WenyanRuntime runtime);

    @Override
    public String toString() {
        return name;
    }
}
