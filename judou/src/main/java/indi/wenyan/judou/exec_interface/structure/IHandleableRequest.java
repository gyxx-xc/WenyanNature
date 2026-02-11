package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.runtime.WenyanThread;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;

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
