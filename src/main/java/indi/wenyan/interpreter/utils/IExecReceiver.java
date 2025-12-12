package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.JavacallRequest;

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
