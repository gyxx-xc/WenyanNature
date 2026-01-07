package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public record JavacallRequest(
        IWenyanPlatform platform,
        IWenyanDevice device,
        WenyanThread thread,

        IRawRequest request,
        IWenyanValue self,
        List<IWenyanValue> args
) implements IHandleableRequest {

    @Override
    public boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException {
        return request.handle(context, this);
    }

    @FunctionalInterface
    public interface IRawRequest {
        boolean handle(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanThrowException;
    }
}
