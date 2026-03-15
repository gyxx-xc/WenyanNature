package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.Either;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ImportRequest implements BaseHandleableRequest {
    @Getter
    WenyanRunner thread;
    ImportFunction getPackage;
    String packageName;

    public ImportRequest(WenyanRunner thread, ImportFunction getPackage, List<IWenyanValue> args) throws WenyanException {
        this.thread = thread;
        this.getPackage = getPackage;
        if (args.size() != 1) {
            throw new WenyanException("参数错误");
        }
        this.packageName = args.getFirst().as(WenyanString.TYPE).value();
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        var packageOrCode = getPackage.getPackage(context, packageName);
        if (packageOrCode.left().isPresent())
            thread().getCurrentRuntime().pushReturnValue(packageOrCode.left().get());
        if (packageOrCode.right().isPresent())
            thread().call(WenyanRuntime.ofImportCode(packageOrCode.right().get(), thread.getCurrentRuntime()));
        thread().unblock();
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
         * @throws WenyanException if the package cannot be found or accessed
         */
        Either<WenyanPackage, String> getPackage(IHandleContext context, String packageName) throws WenyanException;
    }
}
