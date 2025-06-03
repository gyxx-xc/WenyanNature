package indi.wenyan.interpreter.structure;

import java.util.Stack;

public class WenyanThread {
    public final Stack<WenyanRuntime> runtimes = new Stack<>();
    public WenyanThread() {
    }

    public void add(WenyanRuntime runtime) {
        runtimes.push(runtime);
    }
    public WenyanRuntime cur() {
        return runtimes.peek();
    }
    public void ret() {
        runtimes.pop();
    }
}
