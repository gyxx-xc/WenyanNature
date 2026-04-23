package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.UtilManager;
import indi.wenyan.judou.utils.language.JudouExceptionText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExecQueue {
    private final int MAX_QUEUE_SIZE = UtilManager.getConfig().getMaxQueueSize();
    private final int MAX_QUEUE_SIZE_PER_TICK = UtilManager.getConfig().getMaxQueueSizePerTick();

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
    public synchronized void receive(IHandleableRequest request) throws WenyanException {
        if (queue.size() > MAX_QUEUE_SIZE) {
            throw new WenyanException(JudouExceptionText.QueueFull.string());
        }
        queue.add(request);
    }

    /**
     * Handles all queued requests in the current context.
     *
     * @param context the handling context, used to manage execution state
     */
    public void handle(IHandleContext context) {
        // Collects requests that could not be processed in this tick
        Collection<IHandleableRequest> undoneRequests = new ArrayList<>();
        for (int i = 0; i < MAX_QUEUE_SIZE_PER_TICK && !queue.isEmpty(); i++) {
            IHandleableRequest request = queue.remove();
            if (!request.run(platform, context)) {
                undoneRequests.add(request);
            }
        }
        queue.addAll(undoneRequests); // These are for next tick
    }
}
