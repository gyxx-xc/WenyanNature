package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.utils.WenyanThreading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExecQueue {
    private final Queue<IHandleableRequest> queue = new ConcurrentLinkedQueue<>();

    /**
     * Receives a JavacallContext request and adds it to the queue.
     *
     * @param request the JavacallContext request to be added to the queue
     */
    @WenyanThreading
    public void receive(IHandleableRequest request) {
        queue.add(request);
    }

    /**
     * Handles all queued requests in the current context.
     *
     * @param context the handling context, used to manage execution state
     */
    public void handle(IHandleContext context) {
        if (queue.size() > WenyanProgram.MAX_THREAD) {
            throw new WenyanException.WenyanUnreachedException();
        }

        // Collects requests that could not be processed in this tick
        Collection<IHandleableRequest> undoneRequests = new ArrayList<>();
        while (!queue.isEmpty()) {
            IHandleableRequest request = queue.remove();
            try {
                request.platform().notice(request, context);
                boolean done = request.handle(context);
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
