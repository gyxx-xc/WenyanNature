package indi.wenyan.interpreter.exec_interface;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.JavacallRequest;
import indi.wenyan.interpreter.runtime.WenyanRuntime;

/**
 * Interface representing a platform that can execute Wenyan code and send
 * execute command to Wenyan devices
 */
public interface IWenyanPlatform extends IExecReceiver{
    /**
     * Accepts and processes a JavacallContext, and showing effect if needed
     *
     * @param request The request to process
     */
    default void notice(JavacallRequest request, IHandleContext context) {}

    /**
     * Initializes the platform environment for the Wenyan runtime
     * @param baseEnvironment The base runtime environment
     */
    void initEnvironment(WenyanRuntime baseEnvironment);
}
