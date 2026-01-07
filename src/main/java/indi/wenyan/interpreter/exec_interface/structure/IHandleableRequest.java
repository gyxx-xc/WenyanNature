package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public interface IHandleableRequest {
    IWenyanPlatform platform();
    WenyanThread thread();
    IWenyanValue self();
    List<IWenyanValue> args();

    boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException;

    @FunctionalInterface
    interface IRawRequest {
        boolean handle(IHandleContext context, IHandleableRequest request) throws WenyanException.WenyanThrowException;
    }
}
