package indi.wenyan.judou.exec_interface;

import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IExecQueue;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.utils.UtilManager;
import indi.wenyan.judou.api.values.exception.WenyanException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExecQueue implements IExecQueue {
    private final int MAX_QUEUE_SIZE = UtilManager.getConfig().getMaxQueueSize();
    private final int MAX_QUEUE_SIZE_PER_TICK = UtilManager.getConfig().getMaxQueueSizePerTick();

    private final IWenyanPlatform platform;

    private final Queue<IHandleableRequest> queue = new ConcurrentLinkedQueue<>();

    public ExecQueue(IWenyanPlatform platform) {
        this.platform = platform;
    }

    @Override
    public synchronized void receive(IHandleableRequest request) throws WenyanException {
        if (queue.size() > MAX_QUEUE_SIZE) {
            throw new WenyanException(JudouExceptionText.QueueFull.string());
        }
        queue.add(request);
    }

    @Override
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
