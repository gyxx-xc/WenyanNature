package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.Stack;
import java.util.concurrent.Semaphore;

public class WenyanThread {
    public final Stack<WenyanRuntime> runtimes = new Stack<>();
    public boolean isRunning = true;
    public int assignedSteps = 0;
    public final WenyanProgram program;
    public State state = State.READY;

    public enum State {
        READY,
        BLOCKED
    }

    public WenyanThread(WenyanProgram program) {
        this.program = program;
    }

    public void programLoop(Semaphore accumulatedSteps) throws InterruptedException {
        while (true) {
            WenyanRuntime runtime = currentRuntime();

            if (runtime.programCounter >= runtime.bytecode.size()) {
                isRunning = false;
                return;
            }

            WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);

            int needStep = code.code().getStep(code.arg(), this);
            if (assignedSteps < needStep) {
                return; //switch
            }
            assignedSteps -= needStep;
            accumulatedSteps.acquire(needStep);

            code.code().exec(code.arg(), this);

//        System.out.println(runtime.programCounter + ": " + code);
//        System.out.println(runtime.processStack);
//        System.out.println(runtime.resultStack);

            if (!runtime.PCFlag)
                runtime.programCounter++;
            runtime.PCFlag = false;
        }
    }

    public void block() {
        if (state == State.READY) {
            state = State.BLOCKED;
            assignedSteps = 0;
        } else {
            throw new RuntimeException("unreached");
        }
    }

    public void add(WenyanRuntime runtime) {
        runtimes.push(runtime);
    }
    public WenyanRuntime currentRuntime() {
        return runtimes.peek();
    }
    public void ret() {
        runtimes.pop();
    }

    public WenyanValue getGlobalVariable(String id) {
        WenyanValue value = null;
        for (int i = runtimes.size()-1; i >= 0; i --) {
            if (runtimes.get(i).variables.containsKey(id)) {
                value = runtimes.get(i).variables.get(id);
                break;
            }
        }
        if (value == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString()+id);
        return value;
    }
}
