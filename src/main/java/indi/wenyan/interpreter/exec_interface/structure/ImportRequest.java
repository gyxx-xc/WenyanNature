package indi.wenyan.interpreter.exec_interface.structure;

import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;

import java.util.List;

public record ImportRequest (
        WenyanThread thread,
        IWenyanPlatform platform,

        ImportFunction getPackage,
        List<IWenyanValue> args
) implements IHandleableRequest {
    public IWenyanValue self() {
        throw new WenyanException("unreached");
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException.WenyanThrowException {
        String packageName = args.getFirst().as(WenyanString.TYPE).value();
        WenyanPackage execPackage = getPackage.getPackage(context, packageName);

        if (args.isEmpty()) {
            throw new WenyanException("参数错误");
        }

        if (args.size() == 1) {
            thread.currentRuntime().setVariable(packageName, execPackage);
            thread.currentRuntime().resultStack.push(execPackage);
        } else {
            for (IWenyanValue arg : args.subList(1, args.size())) {
                String id = arg.as(WenyanString.TYPE).value();
                // not found error will throw inside getAttribute
                thread.currentRuntime().setVariable(id,
                        execPackage.getAttribute(id));
            }
        }
        return true;
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
