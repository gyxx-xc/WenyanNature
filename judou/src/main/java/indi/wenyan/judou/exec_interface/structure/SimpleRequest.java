package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

public record SimpleRequest(WenyanRunner thread, IWenyanValue self,
                            List<IWenyanValue> args, SimpleHandleFunction handler)
        implements BaseHandleableRequest, IArgsRequest {
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
