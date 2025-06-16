package indi.wenyan.interpreter.runtime;

import indi.wenyan.WenyanNature;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
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

    public void programLoop(Semaphore accumulatedSteps) throws InterruptedException {
        while (true) {
            WenyanRuntime runtime = currentRuntime();

            if (runtime.programCounter >= runtime.bytecode.size()) {
                state = State.DYING;
                return;
            }

            WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);

            int needStep = code.code().getStep(code.arg(), this);
            if (assignedSteps < needStep) {
                return; //switch
            }
            assignedSteps -= needStep;
            accumulatedSteps.acquire(needStep);

            try {
                code.code().exec(code.arg(), this);
            } catch (WenyanException e) {
                state = State.DYING;
                WenyanBytecode.Context context = runtime.bytecode.getContext(runtime.programCounter);

                WenyanException.handleException(program.holder, context.line() + ":" + context.column() + " " + e.getMessage());
                return;
            } catch (RuntimeException e) {
                // for debug only
                state = State.DYING;

                WenyanNature.LOGGER.error(e.getMessage());
                WenyanException.handleException(program.holder, "killed");
                return;
            }

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

    public WenyanNativeValue getGlobalVariable(String id) {
        WenyanNativeValue value = null;
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
