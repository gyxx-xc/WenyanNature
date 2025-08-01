package indi.wenyan.interpreter.runtime;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.network.chat.Component;

import java.util.Stack;
import java.util.concurrent.Semaphore;

public class WenyanThread {
    public final Stack<WenyanRuntime> runtimes = new Stack<>();
    public int assignedSteps = 0;
    public final WenyanProgram program;
    public State state = State.READY;

    public enum State {
        READY,
        BLOCKED,
        DYING
    }

    public WenyanThread(WenyanProgram program) {
        this.program = program;
    }

    // This method should be only called by the main thread to run the program loop.
    // since it needs scheduling after return
    public void programLoop(Semaphore accumulatedSteps) throws InterruptedException {
        while (true) {
            WenyanRuntime runtime = currentRuntime();

            if (runtime.programCounter >= runtime.bytecode.size()) {
                die();
                return;
            }

            WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);

            int needStep;
            try {
                needStep = code.code().getStep(code.arg(), this);
            } catch (Exception e) {
                dieWithException(e);
                return;
            }

            if (assignedSteps < needStep) {
                this.yield();
                return; //switch
            }
            assignedSteps -= needStep;
            accumulatedSteps.acquire(needStep);

            try {
                code.code().exec(code.arg(), this);
            } catch (Exception e) {
                dieWithException(e);
                return;
            }
            if (!runtime.PCFlag)
                runtime.programCounter++;
            runtime.PCFlag = false;

            if (state != State.READY) {
                return; // yield
            }
        }
    }

    public void dieWithException(Exception e) {
        if (e instanceof WenyanException) {
            WenyanBytecode.Context context = currentRuntime().bytecode.getContext(currentRuntime().programCounter);
            WenyanException.handleException(program.holder, context.line() + ":" + context.column() + " " +
                    context.content() + " " + e.getMessage());
        } else {
            // for debug only
            WenyanProgramming.LOGGER.error("WenyanThread died with an unexpected exception", e);
            WenyanProgramming.LOGGER.error(e.getMessage());
            WenyanException.handleException(program.holder, "killed");
        }
        die();
    }

    public void block() {
        if (state == State.READY) {
            state = State.BLOCKED;
            assignedSteps = 0;
        } else {
            throw new RuntimeException("unreached");
        }
    }

    public void yield() {
        if (state == State.READY) {
            program.readyQueue.add(this);
        }
    }

    public void die() {
        if (state == State.DYING)
            throw new RuntimeException("WenyanThread is already dying");
        program.runningCounter.decrementAndGet();
        state = State.DYING;
    }

    public void call(WenyanRuntime runtime) {
        runtimes.push(runtime);
    }
    public WenyanRuntime currentRuntime() {
        return runtimes.peek();
    }
    public void ret() {
        runtimes.pop();
    }

    public IWenyanValue getGlobalVariable(String id) {
        IWenyanValue value = null;
        for (int i = runtimes.size()-1; i >= 0; i --) {
            if (runtimes.get(i).variables.containsKey(id)) {
                value = runtimes.get(i).variables.get(id);
                break;
            }
        }
        if (value == null)
            throw new WenyanException(Component.translatable("error.wenyan_programming.variable_not_found_").getString()+id);
        return value;
    }
}
