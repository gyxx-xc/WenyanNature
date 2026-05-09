package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

public record SimpleRequest(IWenyanRunner thread, IWenyanValue self,
                            List<IWenyanValue> args, SimpleHandleFunction handler)
        implements IBaseHandleableRequest, IArgsRequest {
    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        thread.getCurrentRuntime().pushReturnValue(handler.handle(self, args));
        thread.unblock();
        return true;
    }

    @FunctionalInterface
    public interface SimpleHandleFunction {
        IWenyanValue handle(IWenyanValue self, List<IWenyanValue> args) throws WenyanException;
    }
}
