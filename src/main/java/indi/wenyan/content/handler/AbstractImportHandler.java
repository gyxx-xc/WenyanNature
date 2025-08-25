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

/**
 * Abstract handler for importing packages in Wenyan programs.
 * Manages import requests in a queue and processes them asynchronously.
 */
public abstract class AbstractImportHandler implements IWenyanFunction {
    private final ConcurrentLinkedQueue<ImportRequest> requestQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        requestQueue.add(new ImportRequest(thread, argsList));
        thread.block();
    }

    /**
     * Processes all pending import requests in the queue.
     * Resolves package imports and either pushes the package to the result stack
     * or imports specific attributes from the package.
     */
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

    /**
     * Retrieves a package by its name.
     *
     * @param packageName the name of the package to retrieve
     * @return the requested package
     * @throws WenyanException.WenyanThrowException if the package cannot be found or accessed
     */
    public abstract WenyanPackage getPackage(String packageName) throws WenyanException.WenyanThrowException;

    /**
     * Represents an import request with the thread and arguments.
     */
    record ImportRequest(WenyanThread thread, List<IWenyanValue> args) {}
}
