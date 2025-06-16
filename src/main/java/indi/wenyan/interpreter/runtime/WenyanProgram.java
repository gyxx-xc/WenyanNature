package indi.wenyan.interpreter.runtime;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.checker.CraftingAnswerChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.compiler.visitor.WenyanVisitor;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.world.entity.player.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class WenyanProgram {

    public String code;

    public final WenyanBytecode baseBytecode = new WenyanBytecode();
    public final WenyanRuntime baseEnvironment;

    public WenyanThread mainThread = new WenyanThread(this);
    public Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();
    public Queue<JavacallContext> requestThreads = new ConcurrentLinkedQueue<>();
    private final Semaphore accumulatedSteps = new Semaphore(0);

    private Thread programJavaThread;

    // STUB: used in error handler, might changed
    public Player holder;

    public final JavacallContext.RunnerWarper<?> warper;

    private static final int SWITCH_COST = 5;
    private static final int SWITCH_STEP = 10;

    public WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder) {
        this(code, baseEnvironment, holder,
                new JavacallContext.NullRunnerWarper(null));
    }

    public WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder,
                         HandRunnerEntity runner) {
        this(code, baseEnvironment, holder,
                new JavacallContext.HandRunnerWarper(runner));
    }

    public WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder,
                         BlockRunner runner) {
        this(code, baseEnvironment, holder,
                new JavacallContext.BlockRunnerWarper(runner));
    }

    public WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder,
                         CraftingAnswerChecker checker) {
        this(code, baseEnvironment, holder,
                new JavacallContext.CraftingAnswerWarper(checker));
    }

    private WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder,
                         JavacallContext.RunnerWarper<?> warper) {
        this.code = code;
        this.warper = warper;
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        visitor.visit(WenyanVisitor.program(code));
        this.baseEnvironment = baseEnvironment;
        this.holder = holder;
    }

    public void run() {
        mainThread.add(baseEnvironment);
        mainThread.add(new WenyanRuntime(baseBytecode));
        readyQueue.add(mainThread);
        programJavaThread = new Thread(() -> scheduler(this));
        programJavaThread.start();
    }

    public void handle() {
        while (!requestThreads.isEmpty()) {
            JavacallContext request = requestThreads.poll();
            try {
                request.handler().handleWarper(request);
                request.thread().state = WenyanThread.State.READY;
                request.thread().program.readyQueue.add(request.thread());
            } catch (WenyanException.WenyanThrowException | WenyanException e) {
                request.thread().state = WenyanThread.State.DYING;
                WenyanException.handleException(holder, e.getMessage());
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
        programJavaThread.interrupt();
    }

    public boolean isRunning() {
        return mainThread.state != WenyanThread.State.DYING;
    }

    // this on other thread
    public static void scheduler(WenyanProgram program) {
        try {
            while (program.mainThread.state != WenyanThread.State.DYING) {
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
        WenyanProgram program = new WenyanProgram(
                """
吾有一列名之曰「a 」
充「a 」以一以一以一以一以一以一
凡「a 」中之「b 」
書「b 」
云云""",
                WenyanPackages.WENYAN_BASIC_PACKAGES
        , null);
        System.out.println(program.baseBytecode);
        program.run();
        while (program.isRunning()) {
            program.step();
            program.handle();
        }
    }
}
