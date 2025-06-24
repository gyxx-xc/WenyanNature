package indi.wenyan.interpreter.runtime;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.content.checker.AnsweringChecker;
import indi.wenyan.content.checker.LabyrinthChecker;
import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.compiler.visitor.WenyanVisitor;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class WenyanProgram {

    public final String code;

    public final WenyanBytecode baseBytecode = new WenyanBytecode();
    public final WenyanRuntime baseEnvironment;

    public final WenyanThread mainThread = new WenyanThread(this);
    public final Queue<WenyanThread> readyQueue = new ConcurrentLinkedQueue<>();
    public final Queue<JavacallContext> requestThreads = new ConcurrentLinkedQueue<>();
    private final Semaphore accumulatedSteps = new Semaphore(0);

    private Thread programJavaThread;

    // STUB: used in error handler, might changed
    public final Player holder;

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
                         AnsweringChecker checker) {
        this(code, baseEnvironment, holder,
                new JavacallContext.CraftingAnswerWarper(checker));
    }

    private WenyanProgram(String code, WenyanRuntime baseEnvironment, Player holder,
                         JavacallContext.RunnerWarper<?> warper) {
        this.code = code;
        this.warper = warper;
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        try {
            visitor.visit(WenyanVisitor.program(code));
        } catch (WenyanException e) {
            WenyanException.handleException(holder, e.getMessage());
        }
        this.baseEnvironment = baseEnvironment;
        this.holder = holder;
    }

    public void run() {
        mainThread.call(baseEnvironment);
        mainThread.call(new WenyanRuntime(baseBytecode));
        readyQueue.add(mainThread);
        programJavaThread = new Thread(() -> scheduler(this));
        programJavaThread.start();
    }

    public void handle() {
        while (!requestThreads.isEmpty()) {
            JavacallContext request = requestThreads.poll();
            try {
                request.thread().currentRuntime().processStack
                        .push(request.handler().handle(request));
                request.thread().unblock();
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
                thread.assignedSteps += SWITCH_STEP;
                thread.programLoop(program.accumulatedSteps);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        WenyanProgram program = new WenyanProgram(
                """
                        吾有一物。名之曰「a」。其物如是。
                                                  	物之造者術是術曰。
                                                  		夫一名之曰己之「「a 」」
                                                  	是謂造之術也。
                                                  是謂「a」之物也。
                        
                                                  吾有一物繼「a」。名之曰「b」。其物如是。
                                                  	物之造者術是術曰。
                                                  		施父之造
                                                  		夫二名之曰己之「「a 」」
                                                  		夫一名之曰己之「「b 」」
                                                  	是謂造之術也。
                                                  是謂「b」之物也。
                        
                                                  造「a」名之曰「a 」
                                                  施「a」名之曰「a1 」
                                                  造「b」名之曰「b 」
                                                  施「b」名之曰「b1 」
                        
                                                  書「a 」之「「a 」」
                                                  書「a1 」之「「a 」」
                                                  書「b 」之「「a 」」
                                                  書「b1 」之「「a 」」
                                                  書「b 」之「「b 」」
                                                  書「b1 」之「「b 」」
                        
                                                  昔之「b 」之「「a 」」者今三是矣
                        
                                                  書「a 」之「「a 」」
                                                  書「b 」之「「a 」」
                        """,
                WenyanPackages.WENYAN_BASIC_PACKAGES
        , null);
        program.run();
        while (true) {
            program.step();
            program.handle();
            if (!program.isRunning()) {
                System.out.println("Program finished.");
                break;
            }
        }
    }
}
