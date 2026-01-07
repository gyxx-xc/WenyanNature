package indi.wenyan.interpreter.exec_interface;

public interface IWenyanDevice {
    /**
     * @return The package containing functions accessible to this device
     */
    HandlerPackageBuilder.RawHandlerPackage getExecPackage();

    /**
     * @return The name of this device's package
     */
    String getPackageName();
}
