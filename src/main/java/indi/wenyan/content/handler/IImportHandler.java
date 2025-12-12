package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IHandleContext;

/**
 * Abstract handler for importing packages in Wenyan programs.
 * Manages import requests in a queue and processes them asynchronously.
 */
public interface IImportHandler extends IExecCallHandler {

    /**
     * Retrieves a package by its name.
     *
     * @param context the handling context, used to manage execution state
     * @param packageName the name of the package to retrieve
     * @return the requested package
     * @throws WenyanException.WenyanThrowException if the package cannot be found or accessed
     */
    WenyanPackage getPackage(IHandleContext context, String packageName) throws WenyanException.WenyanThrowException;

    default boolean handle(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanThrowException {
        WenyanPackage execPackage =
                getPackage(context, request.args().getFirst().as(WenyanString.TYPE).value());
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
        return true;
    }
}
