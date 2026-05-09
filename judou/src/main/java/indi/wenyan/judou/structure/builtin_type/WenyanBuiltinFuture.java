package indi.wenyan.judou.structure.builtin_type;

import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WenyanBuiltinFuture implements IWenyanValue {
    @Nullable
    private IWenyanValue returnValue = null;
    private final List<IWenyanRunner> waitingThreads = new ArrayList<>();

    public static final WenyanType<WenyanBuiltinFuture> TYPE = new WenyanType<>(JudouTypeText.BuiltinFuture.string(), WenyanBuiltinFuture.class);

    public boolean addWaitingThread(IWenyanRunner thread) throws WenyanUnreachedException {
        if (returnValue == null) {
            waitingThreads.add(thread);
            return true;
        }
        thread.getCurrentRuntime().pushReturnValue(returnValue);
        return false;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    public void onRunnerReturn(IWenyanRunner runner, IWenyanValue value) throws WenyanUnreachedException {
        returnValue = value;
        for (IWenyanRunner thread : waitingThreads) {
            try {
                thread.getCurrentRuntime().pushReturnValue(value);
                thread.unblock();
            } catch (WenyanUnreachedException ignore) {
                // should not happen
                // or maybe? if the program stopped when waiting, ignore it then.
            }
        }
        waitingThreads.clear();
        runner.getFrameManager().ret();
    }
}
