package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.runtime.IFrameManager;
import indi.wenyan.judou.api.utils.UtilManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameManagerImpl implements IFrameManager<WenyanFrame> {
    public final int maxRecursionDepth = UtilManager.getConfig().getMaxRecursionDepth();
    @Nullable WenyanFrame currentRuntime;
    int recursionDepth = 0;

    public FrameManagerImpl(@NotNull WenyanFrame mainRuntime) {
        currentRuntime = mainRuntime;
    }

    @Override
    public @NotNull WenyanFrame getCurrentRuntimeException() throws WenyanUnreachedException {
        if (currentRuntime == null)
            throw new WenyanUnreachedException();
        return currentRuntime;
    }

    @Override
    public @Nullable WenyanFrame getCurrentRuntime() {
        return currentRuntime;
    }

    @Override
    public void call(WenyanFrame runtime) throws WenyanException {
        recursionDepth++;
        if (recursionDepth > maxRecursionDepth) {
            throw new WenyanException(JudouExceptionText.RecursionDepthTooDeep.string());
        }
        currentRuntime = runtime;
    }

    @Override
    public void ret() throws WenyanUnreachedException {
        recursionDepth--;
        currentRuntime = getCurrentRuntimeException().getReturnRuntime();
    }
}
