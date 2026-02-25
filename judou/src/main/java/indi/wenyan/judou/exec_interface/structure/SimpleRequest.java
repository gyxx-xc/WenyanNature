package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

public record SimpleRequest(WenyanThread thread, IWenyanValue self, List<IWenyanValue> args, SimpleHandleFunction handler) implements BaseHandleableRequest {
    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        thread.currentRuntime().pushReturnValue(handler.handle(self, args));
        thread.unblock();
        return true;
    }

    @FunctionalInterface
    public interface SimpleHandleFunction {
        IWenyanValue handle(IWenyanValue self, List<IWenyanValue> args) throws WenyanException;
    }
}
