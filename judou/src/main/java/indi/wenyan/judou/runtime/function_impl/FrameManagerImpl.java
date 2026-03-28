package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IFrameManager;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.language.JudouExceptionText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrameManagerImpl implements IFrameManager<WenyanFrame> {
    public final int maxRecursionDepth = 3000;
    @Nullable WenyanFrame currentRuntime;
    int recursionDepth = 0;

    public FrameManagerImpl(@NotNull WenyanFrame mainRuntime) {
        currentRuntime = mainRuntime;
    }

    @Override
    public @NotNull WenyanFrame getCurrentRuntime() throws WenyanUnreachedException {
        if (currentRuntime == null)
            throw new WenyanUnreachedException();
        return currentRuntime;
    }

    @Override
    public WenyanFrame getNullableCurrentRuntime() {
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
        currentRuntime = getCurrentRuntime().getReturnRuntime();
    }
}
