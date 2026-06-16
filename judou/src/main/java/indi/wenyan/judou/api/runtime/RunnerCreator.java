package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.compile.IWenyanBytecode;
import indi.wenyan.judou.api.utils.UtilManager;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.function_impl.IThreadHolder;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanSwitchInlineRunner;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum RunnerCreator {
    ;

    @ApiStatus.Internal
    @Contract("_, _ -> new")
    public static @NotNull <T extends IWenyanScheduler.IWenyanThread> IThreadHolder<T> newRunner(WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        if (UtilManager.getConfig().useLegacyRunner()) {
            //noinspection deprecation
            return new WenyanRunner<>(mainRuntime, globalResolver);
        } else {
            return new WenyanSwitchInlineRunner<>(mainRuntime, globalResolver);
        }
    }

    public static <T extends IWenyanScheduler.IWenyanThread> void createThread(Supplier<IWenyanScheduler<T>> scheduler, IWenyanBytecode mainRuntime, IGlobalResolver globalResolver) throws WenyanException {
        scheduler.get().create(newRunner(WenyanFrame.ofCode(mainRuntime), globalResolver));
    }
}
