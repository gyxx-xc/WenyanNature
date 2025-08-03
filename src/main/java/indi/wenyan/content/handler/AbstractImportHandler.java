package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanProgram;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractImportHandler implements IWenyanFunction {
    private final ConcurrentLinkedQueue<ImportRequest> requestQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        requestQueue.add(new ImportRequest(thread, argsList));
        thread.block();
    }

    public void handle() {
        while (!requestQueue.isEmpty()) {
            ImportRequest request = requestQueue.poll();
            try {
                WenyanPackage execPackage =
                        getPackage(request.args().getFirst().as(WenyanString.TYPE).value());
                if (request.args().size() == 1) {
                    // TODO: test needed
                    request.thread().currentRuntime().resultStack.push(execPackage);
                } else {
                    for (IWenyanValue arg : request.args().subList(1, request.args().size())) {
                        String id = arg.as(WenyanString.TYPE).value();
                        // not found error will throw inside getAttribute
                        request.thread().currentRuntime().setVariable(id,
                                execPackage.getAttribute(id));
                    }
                }
            } catch (WenyanException | WenyanException.WenyanThrowException e) {
                request.thread.dieWithException(e);
                continue;
            }
            WenyanProgram.unblock(request.thread);
        }
    }

    @Override
    public WenyanType<?> type() {
        return IWenyanFunction.TYPE;
    }

    public abstract WenyanPackage getPackage(String packageName) throws WenyanException.WenyanThrowException;

    record ImportRequest(WenyanThread thread, List<IWenyanValue> args) {}
}
