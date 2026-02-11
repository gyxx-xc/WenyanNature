package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleableRequest;
import indi.wenyan.interpreter.exec_interface.structure.SimpleRequest;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

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
