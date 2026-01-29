package indi.wenyan.interpreter.exec_interface.structure;

import com.mojang.datafixers.util.Either;
import indi.wenyan.interpreter.exec_interface.IWenyanPlatform;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNull;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@Accessors(fluent = true)
public final class ImportRequest implements IHandleableRequest {
    final WenyanThread thread;
    final IWenyanPlatform platform;
    final ImportFunction getPackage;
    final List<IWenyanValue> args;
    private final String packageName;
    private Status status = Status.FIRST_RUN;
    private Either<WenyanPackage, WenyanThread> packageOrThread;

    public ImportRequest(WenyanThread thread, IWenyanPlatform platform, ImportFunction getPackage, List<IWenyanValue> args) throws WenyanThrowException {
        this.thread = thread;
        this.platform = platform;
        this.getPackage = getPackage;
        this.args = args;
        this.packageName = args.getFirst().as(WenyanString.TYPE).value();
    }

    public IWenyanValue self() {
        return WenyanNull.NULL;
    }

    // logic too complex, impl in Automata
    @Override
    public boolean handle(IHandleContext context) throws WenyanThrowException {
        //noinspection LoopStatementThatDoesntLoop
        while (true)
            switch (status) {
                case FIRST_RUN:
                    packageOrThread = getPackage.getPackage(context, packageName);
                    status = Status.PROCESS_PACKAGE;
                case PROCESS_PACKAGE:
                    if (packageOrThread.right().isPresent()) {
                        status = Status.WAITING;
                    } else if (packageOrThread.left().isPresent()) {
                        returnPackage(packageOrThread.left().get());
                        return true; // end
                    }
                case WAITING:
                    WenyanThread wenyanThread = packageOrThread.right().get();
                    if (wenyanThread.state == WenyanThread.State.DYING) {
                        if (wenyanThread.getMainRuntime().finishFlag) {
                            status = Status.PROCESS_RUNTIME;
                        } else {
                            throw new WenyanException("运行时异常");
                        }
                    } else {
                        // status = Status.WAITING; // jump to itself
                        return false; // goto first line
                    }
                case PROCESS_RUNTIME:
                    returnPackage(new WenyanPackage(packageOrThread.right().get().getMainRuntime().variables));
                    return true; // end
            }
    }

    private void returnPackage(@NotNull WenyanPackage wenyanPackage) throws WenyanThrowException {
        if (args.isEmpty()) {
            throw new WenyanException("参数错误");
        }

        if (args.size() == 1) {
            thread.currentRuntime().setVariable(packageName, wenyanPackage);
            thread.currentRuntime().resultStack.push(wenyanPackage);
        } else {
            for (IWenyanValue arg : args.subList(1, args.size())) {
                String id = arg.as(WenyanString.TYPE).value();
                // not found error will throw inside getAttribute
                thread.currentRuntime().setVariable(id,
                        wenyanPackage.getAttribute(id));
            }
        }
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
         * @throws WenyanThrowException if the package cannot be found or accessed
         */
        Either<WenyanPackage, WenyanThread> getPackage(IHandleContext context, String packageName) throws WenyanThrowException;
    }
}
