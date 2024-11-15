package indi.wenyan.interpreter.utils;

import indi.wenyan.WenyanNature;
import indi.wenyan.interpreter.structure.WenyanControl;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanFunctionEnvironment;
import indi.wenyan.interpreter.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.visitor.WenyanVisitor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.Semaphore;

public class WenyanProgram {
    public Semaphore entitySemaphore;
    public Semaphore programSemaphore;
    public Thread program;
    public String code;
    public Player holder;
    public WenyanFunctionEnvironment baseEnvironment;

    public WenyanProgram(String code, Player holder, WenyanFunctionEnvironment baseEnvironment) {
        this.code = code;
        this.holder = holder;
        this.baseEnvironment = baseEnvironment;
    }

    public void run() {
        if (isRunning())
            return;
        // ready to visit
        programSemaphore = new Semaphore(0);
        entitySemaphore = new Semaphore(0);
        program = new Thread(() -> {
            new WenyanMainVisitor(baseEnvironment, new WenyanControl(entitySemaphore, programSemaphore))
                    .visit(WenyanVisitor.program(code));
            entitySemaphore.release(100000);
        });
        program.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof WenyanException) {
                holder.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), true);
            } else {
                holder.displayClientMessage(Component.literal("Unknown Error, Check server log to show more").withStyle(ChatFormatting.RED), true);
                WenyanNature.LOGGER.error("Error: {}", e.getMessage());
            }
            entitySemaphore.release(100000);
        });

        program.start();
        try {
            entitySemaphore.acquire(1);
        } catch (InterruptedException ignore) {}
    }

    public void step(int num) {
        if (!isRunning()) return;
        boolean flag = true;
        programSemaphore.release(num);
        while (flag) {
            try {
                flag = false;
                entitySemaphore.acquire(num);
            } catch (InterruptedException e) {
                flag = true;
                program.interrupt();
            }
        }
    }

    public void stop() {
        program.interrupt();
    }

    public boolean isRunning(){
        if (program == null)
            return false;
        return program.isAlive();
    }
}
