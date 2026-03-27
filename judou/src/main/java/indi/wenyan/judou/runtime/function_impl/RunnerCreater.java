package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IGlobalResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum RunnerCreater {;
    @Contract("_, _ -> new")
    public static @NotNull IWenyanRunner newRunner(WenyanFrame mainRuntime, IGlobalResolver globalResolver) {
        return new WenyanRunner(mainRuntime, globalResolver);
    }
}
