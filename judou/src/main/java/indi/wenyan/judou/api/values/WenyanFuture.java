package indi.wenyan.judou.api.values;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinAsyncFunction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/// use with the [WenyanBuiltinAsyncFunction].
///
/// hold by caller (in value form, can be transfer), with [#onRunnerReturn] hold by callee
/// 1. callee function inject the return to [#onRunnerReturn]
/// 2. caller add the waiting thread to [#waitingThreads]
public class WenyanFuture implements IWenyanValue {
    @Getter @Nullable private volatile IWenyanValue returnValue;
    private final List<WaitingThread> waitingThreads = new ArrayList<>();

    public static final WenyanType<WenyanFuture> TYPE = new WenyanType<>(JudouTypeText.BuiltinFuture.string(), WenyanFuture.class);

    public void addWaiting(Consumer<IWenyanValue> onReturn) {
        IWenyanValue val = returnValue;
        if (val != null) {
            onReturn.accept(val);
            return;
        }
        synchronized (waitingThreads) {
            val = returnValue;
            if (val == null) {
                waitingThreads.add(new WaitingThread(onReturn));
            } else {
                onReturn.accept(val);
            }
        }
    }

    /// set value and notice all thread waiting this future, should only call once
    public void onRunnerReturn(IWenyanRunner runner, @NotNull IWenyanValue value) throws WenyanUnreachedException {
        List<WaitingThread> toNotify;
        synchronized (waitingThreads) {
            toNotify = new ArrayList<>(waitingThreads);
            waitingThreads.clear();
            returnValue = value;
        }
        for (var thread : toNotify) {
            thread.run(value);
        }
        runner.getFrameManager().ret();
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    private record WaitingThread(Consumer<IWenyanValue> callback) {
        void run(IWenyanValue returnValue) {
            callback.accept(returnValue);
        }
    }
}
