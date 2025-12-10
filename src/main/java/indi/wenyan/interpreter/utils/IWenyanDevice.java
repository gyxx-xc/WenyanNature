package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Interface representing a device that can execute Wenyan command sent by Wenyan platforms
 */
public interface IWenyanDevice {
    /**
     * @return The package containing functions accessible to this device
     */
    WenyanPackage getExecPackage();

    /**
     * @return The name of this device's package
     */
    String getPackageName();

    /**
     * @return The execution queue for this device
     */
    ExecQueue getExecQueue();

    /**
     * Receives a JavacallContext request and adds it to the execution queue
     * @param request The JavacallContext to process
     */
    default void receive(JavacallContext request) {
        getExecQueue().receive(request);
    }

    /**
     * Handles all pending requests in the execution queue
     */
    default void handle() {
        getExecQueue().handle();
    }

    /**
     * For connecting effect only
     *
     * @return The position of this device in the world
     */
    Vec3 getPosition();

    /**
     * Queue for handling JavacallContext requests
     */
    class ExecQueue {

        private final Queue<JavacallContext> queue = new ConcurrentLinkedQueue<>();

        /**
         * Adds a request to the execution queue
         * @param request The JavacallContext to queue
         */
        public void receive(JavacallContext request) {
            queue.add(request);
        }

        /**
         * Processes all pending requests in the queue
         */
        public void handle() {
            Collection<JavacallContext> undoneRequests = new ArrayList<>();
            while (!queue.isEmpty()) {
                JavacallContext request = queue.remove();
                try {
                    request.handler().handle(request).ifPresentOrElse((result) -> {
                        request.thread().currentRuntime().processStack.push(result);
                        WenyanProgram.unblock(request.thread());
                    }, () -> undoneRequests.add(request));
                } catch (WenyanException.WenyanThrowException | WenyanException e) {
                    request.thread().dieWithException(e);
                }
            }
            queue.addAll(undoneRequests); // These are for next tick
        }
    }
}
