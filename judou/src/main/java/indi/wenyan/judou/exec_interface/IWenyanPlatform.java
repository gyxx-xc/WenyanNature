package indi.wenyan.judou.exec_interface;

import indi.wenyan.judou.exec_interface.structure.IHandleContext;
import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.utils.WenyanPackages;

import java.util.HashMap;

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
    default WenyanPackage initEnvironment() {
        var environment = new WenyanPackage(new HashMap<>());
        environment.combine(WenyanPackages.WENYAN_BASIC_PACKAGES);
        return environment;
    }

    String getPlatformName();

    void handleError(String error);
}
