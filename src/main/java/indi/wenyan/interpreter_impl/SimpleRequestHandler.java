package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.api.exec.IRequestCallHandler;
import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

public class SimpleRequestHandler implements IRequestCallHandler {
    private final SimpleRequest.SimpleHandleFunction handler;

    public SimpleRequestHandler(SimpleRequest.SimpleHandleFunction handler) {
        this.handler = handler;
    }

    @Override
    public IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self, List<IWenyanValue> argsList) {
        return new SimpleRequest(thread, self, argsList, handler);
    }
}
