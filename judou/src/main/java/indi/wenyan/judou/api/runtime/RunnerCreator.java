package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.utils.UtilManager;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanSwitchInlineRunner;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum RunnerCreator {
    ;

    @Contract("_, _ -> new")
    public static @NotNull <T extends IWenyanScheduler.IWenyanThread> IThreadHolder<T> newRunner(WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        if (UtilManager.getConfig().useLegacyRunner()) {
            return new WenyanRunner<>(mainRuntime, globalResolver);
        } else {
            return new WenyanSwitchInlineRunner<>(mainRuntime, globalResolver);
        }
    }
}
