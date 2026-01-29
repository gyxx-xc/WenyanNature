package indi.wenyan.interpreter.exec_interface;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.IHandleableRequest;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.utils.WenyanPackages;

/**
 * Interface representing a platform that can execute Wenyan code and send
 * execute command to Wenyan devices
 */
public interface IWenyanPlatform extends IExecReceiver {
    /**
     * Accepts and processes a JavacallContext, and showing effect if needed
     *
     * @param request The request to process
     */
    default void notice(IHandleableRequest request, IHandleContext context) throws WenyanThrowException {}

    /**
     * Initializes the platform environment for the Wenyan runtime
     */
    default WenyanRuntime initEnvironment() {
        var environment = new WenyanRuntime(null);
        environment.importPackage(WenyanPackages.WENYAN_BASIC_PACKAGES);
        return environment;
    }

    String getPlatformName();

    void handleError(String error);
}
