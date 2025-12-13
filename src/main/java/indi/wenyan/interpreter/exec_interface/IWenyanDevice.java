package indi.wenyan.interpreter.exec_interface;

import indi.wenyan.interpreter.structure.values.WenyanPackage;

public interface IWenyanDevice extends IExecReceiver {
    /**
     * @return The package containing functions accessible to this device
     */
    WenyanPackage getExecPackage();

    /**
     * @return The name of this device's package
     */
    String getPackageName();
}
