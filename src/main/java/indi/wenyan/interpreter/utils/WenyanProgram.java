package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.WenyanBytecode;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;

import java.util.LinkedList;
import java.util.Queue;

public class WenyanProgram {

    public String code;

    public final WenyanBytecode baseBytecode = new WenyanBytecode();
    public final WenyanRuntime baseEnvironment;

    public WenyanThread curThreads = new WenyanThread();
    public Queue<WenyanThread> readyThreads = new LinkedList<>();
    private boolean isRunning = false;
    private int accumulatedSteps = 0;

    public WenyanProgram(String code, WenyanRuntime baseEnvironment) {
        this.code = code;
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        visitor.visit(WenyanVisitor.program(code));
        this.baseEnvironment = baseEnvironment;
    }

    public void run() {
        curThreads.add(new WenyanRuntime(baseBytecode));
        readyThreads.add(curThreads);
        isRunning = true;
    }

    public void step() {
        step(1);
    }

    public void step(int steps) {
        accumulatedSteps += steps;
        while (accumulatedSteps > 0 && isRunning) {
            WenyanRuntime runtime = curThreads.cur();

            if (runtime.programCounter >= runtime.bytecode.size()) {
                isRunning = false;
                return;
            }

            WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);

            int needStep = code.code().getStep(code.arg(), this);
            if (needStep > accumulatedSteps) break;
            accumulatedSteps -= needStep;
            code.code().exec(code.arg(), this);

//        System.out.println(runtime.programCounter + ": " + code);
//        System.out.println(runtime.processStack);
//        System.out.println(runtime.resultStack);

            if (!runtime.PCFlag)
                runtime.programCounter++;
            runtime.PCFlag = false;
        }
    }

    public void stop() {
    }

    public boolean isRunning() {
        return isRunning;
    }

    public static void main(String[] args) {
        WenyanProgram program = new WenyanProgram("""
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
                """, WenyanPackages.WENYAN_BASIC_PACKAGES);
        System.out.println(program.baseBytecode);
        program.run();
//        while (program.isRunning)
            program.step(2147483647);
    }
}
