package indi.wenyan.interpreter_impl;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.compile.WenyanCompiler;
import indi.wenyan.judou.api.exec.request.IBaseHandleableRequest;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.Either;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.WenyanPackage;
import indi.wenyan.judou.api.values.primitive.WenyanString;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ImportRequest implements IBaseHandleableRequest {
    @Getter
    IWenyanRunner thread;
    ImportFunction getPackage;
    String packageName;

    public ImportRequest(IWenyanRunner thread, ImportFunction getPackage, List<IWenyanValue> args) throws WenyanException {
        this.thread = thread;
        this.getPackage = getPackage;
        if (args.size() != 1) {
            throw new WenyanException(JudouExceptionText.ArgsNumWrong.string(1, args.size()));
        }
        this.packageName = args.getFirst().as(WenyanString.TYPE).value();
    }

    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        var packageOrCode = getPackage.getPackage(context, packageName);
        if (packageOrCode.left().isPresent())
            thread().getCurrentRuntime().pushReturnValue(packageOrCode.left().get());
        if (packageOrCode.right().isPresent()) {
            var bytecode = new WenyanCompiler().compile(packageOrCode.right().get());
            thread().getFrameManager().call(WenyanFrame.ofImportCode(bytecode.bytecode(), bytecode.exportedValues(), thread.getCurrentRuntime()));
        }
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
