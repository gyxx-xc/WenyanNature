package indi.wenyan.judou.api.exec.structure;

import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.exec_interface.ExecQueue;

/// FIFO queue for processing async requests during game tick.
public interface IExecQueue {
    /// Receives a JavacallContext request and adds it to the queue.
    ///
    /// @param request the JavacallContext request to be added to the queue
    void receive(IHandleableRequest request) throws WenyanException;

    /// Handles all queued requests in the current context.
    ///
    /// @param context the handling context, used to manage execution state
    void handle(IHandleContext context);

    static IExecQueue create(IWenyanPlatform platform) {
        return new ExecQueue(platform);
    }
}
