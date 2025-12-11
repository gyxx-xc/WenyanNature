package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue for handling JavacallContext requests
 */
public class ExecQueue implements IExecQueue {

    private final Queue<JavacallContext> queue = new ConcurrentLinkedQueue<>();

    @Override
    @WenyanThreading
    public void receive(JavacallContext request) {
        queue.add(request);
    }

    @Override
    public void handle(IHandleContext context) {
        Collection<JavacallContext> undoneRequests = new ArrayList<>();
        while (!queue.isEmpty()) {
            JavacallContext request = queue.remove();
            try {
                boolean done = request.handler().handle(request);
                if (done) {
                    WenyanProgram.unblock(request.thread());
                } else {
                    undoneRequests.add(request);
                }
            } catch (WenyanException.WenyanThrowException | WenyanException e) {
                request.thread().dieWithException(e);
            }
        }
        queue.addAll(undoneRequests); // These are for next tick
    }
}
