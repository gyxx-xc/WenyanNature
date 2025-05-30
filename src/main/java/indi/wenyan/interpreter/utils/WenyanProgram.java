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

    private boolean isRunning = false;

    public WenyanProgram(String code, Player holder, WenyanRuntime baseEnvironment) {
        this.code = code;
        this.baseBytecode = new WenyanBytecode();
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(baseBytecode));
        visitor.visit(WenyanVisitor.program(code));
        this.baseEnvironment = baseEnvironment;
    }

    public void run() {
        runtime = new WenyanRuntime(baseEnvironment, baseBytecode);
        isRunning = true;
    }

    public void step() {
//        System.out.println(runtime.processStack);
//        System.out.println(runtime.resultStack);
//        System.out.println(runtime.programCounter + ": " + runtime.bytecode.get(runtime.programCounter));

        if (runtime.programCounter >= runtime.bytecode.size()) {
            isRunning = false;
            return;
        }

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
            if (isRunning)
                step();
            else
                break;
        }
    }

    public void stop() {
    }

    public boolean isRunning() {
        return isRunning;
    }

    public static void main(String[] args) {
        WenyanProgram program = new WenyanProgram("""
                吾有一物。名之曰「精衛」。其物如是。
                	物之「「名」」者。言曰「「女娃」」。
                	物之「「足數」」者。數曰二。
                	物之「「喙數」」者。數曰一。
                是謂「精衛」之物也。
                
                吾有一術。名之曰「造物之術」。欲行是術。必先得一言。曰「甲」。乃行是術曰。
                	吾有一物。名之曰「乙」。其物如是。
                		物之「「丙」」者。言曰「甲」。
                		物之「「丁」」者。數曰四。
                	是謂「乙」之物也。
                	乃得「乙」。
                是謂「造物之術」之術也。
                
                施「造物之術」於「「某甲」」。名之曰「戊」。
                昔之「戊」之「「丁」」者。今五是矣。書「戊」。
                昔之「戊」之「「丁」」者。今不復存矣。書「戊」。
                """, null, WenyanPackages.WENYAN_BASIC_PACKAGES);
        int a = 1;
        for (int i = 0; i < 10000000; i++) {
            a = 1;
            a += i;
        }
        System.out.println(program.baseBytecode);
        program.run();
        program.step(2147483647); // 2^31 - 1
    }
}
