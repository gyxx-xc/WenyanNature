package indi.wenyan.judou.exec_interface;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.utils.WenyanPackages;

import java.util.List;

/**
 * Interface representing a platform that can execute Wenyan code and send
 * execute command to Wenyan devices
 */
public interface IWenyanPlatform extends IExecReceiver {
    /**
     * Accepts and processes a JavacallContext, and showing effect if needed
     * @deprecated changed to noticePlatform in BaseHandleableRequest
     *
     * @param request The request to process
     */
    @Deprecated
    default void notice(IHandleableRequest request, IHandleContext context) throws WenyanException {}

    /**
     * Initializes the platform environment for the Wenyan runtime
     */
    default WenyanRuntime initEnvironment() {
        var environment = new WenyanRuntime(null, List.of());
        environment.importPackage(WenyanPackages.WENYAN_BASIC_PACKAGES);
        return environment;
    }

    String getPlatformName();

    void handleError(String error);
}
