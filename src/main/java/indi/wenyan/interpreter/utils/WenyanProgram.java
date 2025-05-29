package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.structure.WenyanBytecode;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import net.minecraft.world.entity.player.Player;

public class WenyanProgram {

    public String code;

    public final WenyanBytecode baseBytecode;
    public final WenyanRuntime baseEnvironment;
    public WenyanRuntime runtime;

    public WenyanProgram(String code, Player holder, WenyanRuntime baseEnvironment) {
        this.code = code;
        this.baseBytecode = new WenyanBytecode();
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        visitor.visit(WenyanVisitor.program(code));
        this.baseEnvironment = baseEnvironment;
    }

    public void run() {
        runtime = new WenyanRuntime(baseEnvironment, baseBytecode);
    }

    public void step() {
//        System.out.println(runtime.processStack);
//        System.out.println(runtime.resultStack);
//        System.out.println(runtime.programCounter + ": " + runtime.bytecode.get(runtime.programCounter));

        WenyanBytecode.Code code = runtime.bytecode.get(runtime.programCounter);
        code.code().exec(code.arg(), runtime);

        if (!runtime.PCFlag)
            runtime.programCounter++;
        runtime.PCFlag = false;

        if (runtime.changeRuntimeFlag)
            runtime = runtime.nextRuntime;
        runtime.changeRuntimeFlag = false;
    }

    public void step(int steps) {
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    public void stop() {
    }

    public boolean isRunning() {
        return false;
    }

    public static void main(String[] args) {
        WenyanProgram program = new WenyanProgram("""
                吾有一術。名之曰「角谷猜想」。
                欲行是術。必先得一數。
                曰「甲」。
                乃行是術曰。
                	吾有一術。名之曰「助手」。
                	欲行是術。必先得一數。
                	曰「乙」。
                	乃行是術曰。
                		吾有一數。名之曰「埃」。
                		除「乙」以二。所餘幾何。名之曰「積」。
                		若「積」不等於零者。乘三以「乙」。加其於一。昔之「埃」者。今其是矣。
                		若非。 除二於「乙」。昔之「埃」者。今其是矣。云云。
                		乃得「埃」。
                	是謂「助手」之術也。
                
                	吾有一列。名之曰「回」。充「回」以「甲」。
                	恆為是。
                		若「甲」等於一者。乃止。也。
                		施「助手」於「甲」。昔之「甲」者。今其是矣。
                		充「回」以「甲」。
                	云云。
                	充「回」以一。
                	乃得「回」。
                是謂「角谷猜想」之術也。
                
                施「角谷猜想」於十二。書之。
                施「角谷猜想」於十九。書之。
                施「角谷猜想」於二十七。書之。
                """, null, WenyanPackages.WENYAN_BASIC_PACKAGES);
        int a = 1;
        for (int i = 0; i < 10000000; i++) {
            a = 1;
            a += i;
        }
        System.out.println(a);
        program.run();
        while (true) program.step();
    }
}
