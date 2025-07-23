package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import net.minecraft.world.phys.Vec3;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface IWenyanDevice {
    WenyanRuntime getExecPackage();

    String getPackageName();

    ExecQueue getExecQueue();

    default void receive(JavacallContext request) {
        getExecQueue().receive(request);
    }

    default void handle() {
        getExecQueue().handle();
    }

    Vec3 getPosition();

    class ExecQueue {
        private final Queue<JavacallContext> queue = new ConcurrentLinkedQueue<>();

        public void receive(JavacallContext request) {
            queue.add(request);
        }

        public void handle() {
            while (!queue.isEmpty()) {
                JavacallContext request = queue.remove();
                try {
                    request.thread().currentRuntime().processStack
                            .push(request.handler().handle(request));
                    request.thread().unblock();
                } catch (WenyanException.WenyanThrowException | WenyanException e) {
                    request.thread().die();
                    // TODO: show exception
                    System.out.println(e);
                }
            }
        }
    }
}
