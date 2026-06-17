package indi.wenyan.judou.api.exec.structure;

import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.values.exception.WenyanException;

public interface IExecReceiver {
    /// @return The execution queue for this device
    IExecQueue getExecQueue();

    /// Receives a JavacallContext request and adds it to the execution queue
    ///
    /// @param request The JavacallContext to process
    default void receive(IHandleableRequest request) throws WenyanException {
        getExecQueue().receive(request);
    }

    /// Handles all pending requests in the execution queue
    default void handle(IHandleContext context) {
        getExecQueue().handle(context);
    }
}
