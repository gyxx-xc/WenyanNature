package indi.wenyan.judou.exec_interface;

import indi.wenyan.judou.exec_interface.structure.ExecQueue;
import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.utils.WenyanThreading;

public interface IExecReceiver {
    /**
     * @return The execution queue for this device
     */
    ExecQueue getExecQueue();

    /**
     * Receives a JavacallContext request and adds it to the execution queue
     *
     * @param request The JavacallContext to process
     */
    @WenyanThreading
    default void receive(IHandleableRequest request) {
        getExecQueue().receive(request);
    }

    /**
     * Handles all pending requests in the execution queue
     */
    default void handle(IHandleContext context) {
        getExecQueue().handle(context);
    }
}
