package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.exec_interface.structure.SimpleRequest;
import indi.wenyan.judou.runtime.WenyanThread;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

public class SimpleRequestHandler implements RequestCallHandler {
    private final SimpleRequest.SimpleHandleFunction handler;

    public SimpleRequestHandler(SimpleRequest.SimpleHandleFunction handler) {
        this.handler = handler;
    }

    @Override
    public IHandleableRequest newRequest(WenyanThread thread, IWenyanValue self, List<IWenyanValue> argsList) {
        return new SimpleRequest(thread, self, argsList, handler);
    }
}
