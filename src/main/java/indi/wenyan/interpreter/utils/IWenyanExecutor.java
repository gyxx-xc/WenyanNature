package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.Queue;

public interface IWenyanExecutor {
    WenyanRuntime getExecPackage();

    String getPackageName();

    ExecQueue getExecQueue();

    Vec3 getPosition();

    class ExecQueue {
        // might change to a thread-safe queue in the future
        private final Queue<JavacallContext> queue = new LinkedList<>();

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
                }
            }
        }
    }
}
