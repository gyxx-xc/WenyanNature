package indi.wenyan.judou.api.exec.structure;

import indi.wenyan.judou.api.values.WenyanPackage;
import indi.wenyan.judou.utils.WenyanPackages;

import java.util.HashMap;

/// Interface representing a platform that can execute Wenyan code and send
/// execute command to Wenyan devices
public interface IWenyanPlatform extends IExecReceiver {
    /// Initializes the platform environment for the Wenyan runtime
    static WenyanPackage initEnvironment() {
        var environment = new WenyanPackage(new HashMap<>());
        environment.combine(WenyanPackages.getWenyanBasicPackage());
        return environment;
    }

    String getPlatformName();

    void handleError(String error);
}
