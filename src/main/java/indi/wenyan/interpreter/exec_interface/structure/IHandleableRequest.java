package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public interface IHandleableRequest {
    WenyanThread thread();
    IWenyanValue self();
    List<IWenyanValue> args();

    boolean handle(IHandleContext context) throws WenyanThrowException;

    @FunctionalInterface
    interface IRawRequest {
        boolean handle(IHandleContext context, IHandleableRequest request) throws WenyanThrowException;
    }
}
