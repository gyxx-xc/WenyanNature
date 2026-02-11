package indi.wenyan.judou.exec_interface;

public interface IWenyanDevice {
    /**
     * @return The package containing functions accessible to this device
     */
    RawHandlerPackage getExecPackage();

    /**
     * @return The name of this device's package
     */
    String getPackageName();
}
