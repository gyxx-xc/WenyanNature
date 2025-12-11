package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.JavacallContext;

public interface IExecQueue {
    /**
     * Adds a request to the execution queue
     *
     * @param request The JavacallContext to queue
     */
    @WenyanThreading
    void receive(JavacallContext request);

    /**
     * Processes all pending requests in the queue
     */
    void handle(IHandleContext context);
}
