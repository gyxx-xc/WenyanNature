package indi.wenyan.content.block;

import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;

public interface IWenyanDevice {
    /// @return The package containing functions accessible to this device
    RawHandlerPackage getExecPackage();

    /// @return The name of this device's package
    String getPackageName();
}
