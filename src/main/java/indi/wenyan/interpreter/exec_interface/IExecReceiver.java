package indi.wenyan.interpreter.exec_interface;

import indi.wenyan.interpreter.exec_interface.structure.ExecQueue;
import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.utils.WenyanThreading;

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
    default void receive(JavacallRequest request) {
        getExecQueue().receive(request);
    }

    /**
     * Handles all pending requests in the execution queue
     */
    default void handle(IHandleContext context) {
        getExecQueue().handle(context);
    }
}
