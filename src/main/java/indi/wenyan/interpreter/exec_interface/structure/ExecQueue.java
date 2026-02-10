package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.utils.WenyanThreading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExecQueue {
    private final IWenyanPlatform platform;

    private final Queue<IHandleableRequest> queue = new ConcurrentLinkedQueue<>();

    public ExecQueue(IWenyanPlatform platform) {
        this.platform = platform;
    }

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
        //noinspection StatementWithEmptyBody
        if (queue.size() > WenyanProgram.MAX_THREAD) {
            // FIXME: unreached
//            request.thread().dieWithException(new WenyanException.WenyanUnreachedException())
        }

        // Collects requests that could not be processed in this tick
        Collection<IHandleableRequest> undoneRequests = new ArrayList<>();
        while (!queue.isEmpty()) {
            IHandleableRequest request = queue.remove();
            try {
                platform.notice(request, context);
                boolean done = request.handle(context);
                if (done) {
                    request.thread().unblock();
                } else {
                    undoneRequests.add(request);
                }
            } catch (WenyanThrowException e) {
                request.thread().dieWithException(e);
            }
        }
        queue.addAll(undoneRequests); // These are for next tick
    }
}
