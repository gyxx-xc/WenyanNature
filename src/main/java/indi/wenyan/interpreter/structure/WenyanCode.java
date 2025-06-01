package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.WenyanProgram;

public abstract class WenyanCode {
    public final String name;

    protected WenyanCode(String name) {
        this.name = name;
    }

    public abstract void exec(int args, WenyanProgram program);

    public int getStep(int args, WenyanProgram program) {
        return 1;
    }

    @Override
    public String toString() {
        return name;
    }
}
