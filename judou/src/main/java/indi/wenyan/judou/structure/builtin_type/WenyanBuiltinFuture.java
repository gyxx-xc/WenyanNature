package indi.wenyan.judou.structure.builtin_type;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WenyanBuiltinFuture implements IWenyanValue {
    @Nullable
    @Getter
    private IWenyanValue returnValue = null;
    private final List<WaitingThread> waitingThreads = new ArrayList<>();

    public static final WenyanType<WenyanBuiltinFuture> TYPE = new WenyanType<>(JudouTypeText.BuiltinFuture.string(), WenyanBuiltinFuture.class);

    public void addWaiting(Consumer<IWenyanValue> onReturn) {
        if (returnValue == null) {
            // FIXME: check for thread safety
            waitingThreads.add(new WaitingThread(null, onReturn));
        } else onReturn.accept(returnValue);
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    public void onRunnerReturn(IWenyanRunner runner, @NotNull IWenyanValue value) throws WenyanUnreachedException {
        returnValue = value;
        for (var thread : waitingThreads) {
            thread.run(returnValue);
        }
        waitingThreads.clear();
        runner.getFrameManager().ret();
    }

    private record WaitingThread(@Nullable IWenyanRunner thread, Consumer<IWenyanValue> callback) {
        void run(IWenyanValue returnValue) {
            try {
                callback.accept(returnValue);
                if (thread != null) thread.unblock();
            } catch (WenyanUnreachedException ignore) {
                // should not happen
                // or maybe? if the program stopped when waiting, ignore it then.
            }
        }
    }
}
