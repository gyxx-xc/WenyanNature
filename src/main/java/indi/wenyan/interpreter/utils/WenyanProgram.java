package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import net.minecraft.world.entity.player.Player;

public class WenyanProgram {

    public String code;

    public final WenyanBytecode bytecode;
    public int programCounter;

    public WenyanProgram(String code, Player holder, WenyanFunctionEnvironment baseEnvironment) {
        this.code = code;
        this.bytecode = new WenyanBytecode();
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(bytecode));
        visitor.visit(WenyanVisitor.program(code));
    }

    public void run() {
    }

    public void step(int num) {
    }

    public void stop() {
    }

    public boolean isRunning() {
        return false;
    }

    public static void main(String[] args) {
        System.out.println("asd".repeat(3));
        WenyanProgram program = new WenyanProgram("""
                夫一
                加其以一
                名之曰「a」
                """, null, null);
        System.out.println(program.bytecode);
    }
}
