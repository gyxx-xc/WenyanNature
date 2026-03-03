package indi.wenyan.judou.exec_interface.structure;

import indi.wenyan.judou.exec_interface.IWenyanPlatform;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanPackage;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import indi.wenyan.judou.utils.Either;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Accessors(fluent = true)
public final class ImportRequest implements BaseHandleableRequest {
    final WenyanRunner thread;
    final IWenyanPlatform platform;
    final ImportFunction getPackage;
    private final String packageName;
    private Status status = Status.FIRST_RUN;
    private Either<WenyanPackage, WenyanRunner> packageOrThread;

    public ImportRequest(WenyanRunner thread, IWenyanPlatform platform, ImportFunction getPackage, List<IWenyanValue> args) throws WenyanException {
        this.thread = thread;
        this.platform = platform;
        this.getPackage = getPackage;
        if (args.size() != 1) {
            throw new WenyanException("参数错误");
        }
        this.packageName = args.getFirst().as(WenyanString.TYPE).value();
    }

    // logic too complex, impl in Automata
    @Override
    public boolean handle(IHandleContext context) throws WenyanException {
        //noinspection LoopStatementThatDoesntLoop
        while (true)
            switch (status) {
                case FIRST_RUN:
                    packageOrThread = getPackage.getPackage(context, packageName);
                    status = Status.PROCESS_PACKAGE;
                    // fallthrough
                case PROCESS_PACKAGE:
                    if (packageOrThread.right().isPresent()) {
                        status = Status.WAITING;
                    } else if (packageOrThread.left().isPresent()) {
                        returnPackage(packageOrThread.left().get());
                        thread().unblock();
                        return true; // end
                    }
                    // fallthrough
                case WAITING:
                    WenyanRunner wenyanThread = packageOrThread.right().get();
                    if (wenyanThread.isDying()) {
                        if (wenyanThread.getMainRuntime().finishFlag) {
                            status = Status.PROCESS_RUNTIME;
                        } else {
                            throw new WenyanException("运行时异常");
                        }
                    } else {
                        // status = Status.WAITING; // jump to itself
                        return false; // goto first line
                    }
                    // fallthrough
                case PROCESS_RUNTIME:
                    returnPackage(getWenyanPackage(packageOrThread.right().get()));
                    thread().unblock();
                    return true; // end
            }
    }

    private @NotNull WenyanPackage getWenyanPackage(WenyanRunner runner) throws WenyanUnreachedException {
        var runtime = runner.getMainRuntime();
        Map<String, IWenyanValue> result = new HashMap<>();
        for (int i = 0; i < runtime.getLocals().size(); i ++) {
            result.put(runner.getExportedIdentifier(i), runtime.getLocals().get(i));
        }
        return new WenyanPackage(result);
    }

    private void returnPackage(@NotNull WenyanPackage wenyanPackage) {
        thread.getCurrentRuntime().pushReturnValue(wenyanPackage);
    }

    private enum Status {
        FIRST_RUN,
        WAITING,
        PROCESS_PACKAGE,
        PROCESS_RUNTIME
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
        Either<WenyanPackage, WenyanRunner> getPackage(IHandleContext context, String packageName) throws WenyanException;
    }
}
