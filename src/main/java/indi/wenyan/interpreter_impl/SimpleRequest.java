package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;
import java.util.function.Consumer;

public record SimpleRequest(IWenyanRunner thread, IWenyanValue self,
                            List<IWenyanValue> args, SimpleHandleFunction handler,
                            Consumer<IWenyanValue> onReturn)
        implements IBaseHandleableRequest, IArgsRequest {
    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        onReturn.accept(handler.handle(self, args));
        thread.unblock();
        return true;
    }

    public static IRequestCallHandler handlerOf(SimpleHandleFunction handler) {
        return new Handler(handler);
    }

    @FunctionalInterface
    public interface SimpleHandleFunction {
        IWenyanValue handle(IWenyanValue self, List<IWenyanValue> args) throws WenyanException;
    }

    private record Handler(SimpleHandleFunction handler) implements IRequestCallHandler {
        @Override
        public IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self, List<IWenyanValue> args, Consumer<IWenyanValue> onReturn) {
            return new SimpleRequest(thread, self, args, handler, onReturn);
        }
    }
}
