package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.IWenyanPlatform;

import java.util.List;
import java.util.Optional;

/**
 * Abstract handler for importing packages in Wenyan programs.
 * Manages import requests in a queue and processes them asynchronously.
 */
public interface IImportHandler extends IJavacallHandler {

    /**
     * Retrieves a package by its name.
     *
     * @param packageName the name of the package to retrieve
     * @return the requested package
     * @throws WenyanException.WenyanThrowException if the package cannot be found or accessed
     */
    WenyanPackage getPackage(String packageName) throws WenyanException.WenyanThrowException;
    Optional<IWenyanPlatform> getPlatform();

    default void handleImport(ImportContext request) throws WenyanException.WenyanThrowException {
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
    }

    @Override
    default void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        if (self != null)
            throw new WenyanException("unreached");

        getPlatform().ifPresentOrElse(
                platform -> platform.receiveImport(
                        new ImportContext(thread, argsList, this)),
                () -> {throw new WenyanException("killed by no platform");}
        );
        thread.block();
    }

    record ImportContext(WenyanThread thread, List<IWenyanValue> args,
                                IImportHandler handler) { }
}
