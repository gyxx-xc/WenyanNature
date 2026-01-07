package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.deprecated_JavacallRequest;

// warper is for adding the info about the device to the program runner,
// which then bring back to the platform, for action that require the device, like check the device status
public interface IHandlerWarper extends IExecCallHandler {
    // check if still available
    boolean check(IHandleContext context, deprecated_JavacallRequest request);
}
