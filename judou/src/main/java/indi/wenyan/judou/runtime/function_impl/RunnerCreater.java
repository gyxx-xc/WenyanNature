package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.IWenyanThread;
import indi.wenyan.judou.utils.UtilManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum RunnerCreater {
    ;

    @Contract("_, _ -> new")
    public static @NotNull <T extends IWenyanThread> IThreadHolder<T> newRunner(WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        if (UtilManager.getConfig().useLegancyRunner()) {
            return new WenyanRunner<>(mainRuntime, globalResolver);
        } else {
            return new WenyanSwitchInlineRunner<>(mainRuntime, globalResolver);
        }
    }
}
