package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.JavacallRequest;

/**
 * Interface representing a platform that can execute Wenyan code and send
 * execute command to Wenyan devices
 */
public interface IWenyanPlatform extends IExecReceiver{
    /**
     * Accepts and processes a JavacallContext, and showing effect if needed
     * It's not on main thread
     *
     * @param request The request to process
     */
    @WenyanThreading
    default void notice(JavacallRequest request) {}

    /**
     * Initializes the platform environment for the Wenyan runtime
     * @param baseEnvironment The base runtime environment
     */
    void initEnvironment(WenyanRuntime baseEnvironment);
}
