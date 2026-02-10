package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

public record SimpleRequest(WenyanThread thread, IWenyanValue self, List<IWenyanValue> args, SimpleHandleFunction handler) implements IHandleableRequest {
    @Override
    public boolean handle(IHandleContext context) throws WenyanThrowException {
        thread.currentRuntime().pushReturnValue(handler.handle(self, args));
        return true;
    }

    @FunctionalInterface
    public interface SimpleHandleFunction {
        IWenyanValue handle(IWenyanValue self, List<IWenyanValue> args) throws WenyanThrowException;
    }
}
