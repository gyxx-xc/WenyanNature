package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WenyanBuiltinFuture implements IWenyanValue {
    @Getter
    private final DummyRuntime dummyRuntime = new DummyRuntime();

    public static final WenyanType<WenyanBuiltinFuture> TYPE = new WenyanType<>("builtin_future", WenyanBuiltinFuture.class);

    public IWenyanValue get() {
        return dummyRuntime.returnValue;
    }

    public boolean addWaitingThread(WenyanRunner thread) {
        if (dummyRuntime.returnValue == null) {
            dummyRuntime.addWaitingThread(thread);
            return true;
        }
        return false;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    private static class DummyRuntime extends WenyanRuntime {
        private IWenyanValue returnValue = null;
        private final List<WenyanRunner> waitingThreads = new ArrayList<>();

        public DummyRuntime() {
            //noinspection DataFlowIssue
            super(null, null, null);
        }

        @Override
        public void pushReturnValue(IWenyanValue value) {
            returnValue = value;
            for (WenyanRunner thread : waitingThreads) {
                try {
                    thread.unblock();
                } catch (WenyanUnreachedException ignore) {}
            }
        }

        public void addWaitingThread(WenyanRunner thread) {
            waitingThreads.add(thread);
        }
    }
}
