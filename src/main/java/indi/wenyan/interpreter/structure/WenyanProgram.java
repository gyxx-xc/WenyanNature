package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.JavaCallCodeWarper;
import indi.wenyan.interpreter.utils.WenyanPackages;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class WenyanProgram {

    public String code;

    public final WenyanBytecode baseBytecode = new WenyanBytecode();
    public final WenyanRuntime baseEnvironment;

    public WenyanThread mainThread = new WenyanThread(this);
    public Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();
    public Queue<JavaCallCodeWarper.Request> requestThreads = new ConcurrentLinkedQueue<>();
    private final Semaphore accumulatedSteps = new Semaphore(0);

    private static final int SWITCH_COST = 1;
    private static final int SWITCH_STEP = 1000;

    public WenyanProgram(String code, WenyanRuntime baseEnvironment) {
        this.code = code;
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        visitor.visit(WenyanVisitor.program(code));
        this.baseEnvironment = baseEnvironment;
    }

    public void run() {
        mainThread.add(baseEnvironment);
        mainThread.add(new WenyanRuntime(baseBytecode));
        readyQueue.add(mainThread);
        new Thread(() -> scheduler(this)).start();
    }

    public void handle() {
        while (!requestThreads.isEmpty()) {
            JavaCallCodeWarper.Request request = requestThreads.poll();
            try {
                request.handle();
            } catch (WenyanException.WenyanThrowException e) {
                throw new WenyanException(e.getMessage());
            }
        }
    }

    public void step() {
        step(1);
    }

    public void step(int steps) {
        accumulatedSteps.release(steps);
    }

    public void stop() {
    }

    public boolean isRunning() {
        return mainThread.isRunning;
    }

    // this on other thread
    public static void scheduler(WenyanProgram program) {
        try {
            while (program.mainThread.isRunning) {
                program.accumulatedSteps.acquire(SWITCH_COST);
                if (program.readyQueue.isEmpty()) {
                    program.accumulatedSteps.drainPermits();
                    continue;
                }

                WenyanThread thread = program.readyQueue.poll();
                thread.assignedSteps = SWITCH_STEP;
                thread.programLoop(program.accumulatedSteps);

                if (thread.state == WenyanThread.State.READY) {
                    program.readyQueue.add(thread);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        WenyanProgram program = new WenyanProgram("""
恆為是
書一
云云
                """, WenyanPackages.WENYAN_BASIC_PACKAGES);
        System.out.println(program.baseBytecode);
        program.run();
        while (program.isRunning()) {
            program.step(10);
            program.handle();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
