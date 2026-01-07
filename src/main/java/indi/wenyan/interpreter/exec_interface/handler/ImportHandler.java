package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.IWenyanDevice;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.exec_interface.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract handler for importing packages in Wenyan programs.
 * Manages import requests in a queue and processes them asynchronously.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ImportHandler extends RequestCallHandler {

    private static final Function<ImportFunction, Supplier<JavacallRequest.IRawRequest>> newRawRequest = (ImportFunction getPackage) -> () ->
    (context, request) -> {
        String packageName = request.args().getFirst().as(WenyanString.TYPE).value();
        WenyanPackage execPackage = getPackage.getPackage(context, packageName);

        if (request.args().isEmpty()) {
            throw new WenyanException("参数错误");
        }

        if (request.args().size() == 1) {
            request.thread().currentRuntime().setVariable(packageName, execPackage);
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
    };

    public ImportHandler(IWenyanPlatform platform, IWenyanDevice device, ImportFunction getPackage) {
        super(platform, device, newRawRequest.apply(getPackage));
    }

    @FunctionalInterface
    public interface ImportFunction {
        /**
         * Retrieves a package by its name.
         *
         * @param context     the handling context, used to manage execution state
         * @param packageName the name of the package to retrieve
         * @return the requested package
         * @throws WenyanException.WenyanThrowException if the package cannot be found or accessed
         */
        WenyanPackage getPackage(IHandleContext context, String packageName) throws WenyanException.WenyanThrowException;
    }
}
