package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import lombok.Getter;

public class WenyanBuiltinFuture implements IWenyanValue {
    private static class DummyRuntime extends WenyanRuntime {
        private IWenyanValue returnValue = null;

        public DummyRuntime() {
            //noinspection DataFlowIssue
            super(null, null, null);
        }

        @Override
        public void pushReturnValue(IWenyanValue value) {
            returnValue = value;
        }
    }

    @Getter
    private final DummyRuntime dummyRuntime = new DummyRuntime();

    public static final WenyanType<WenyanBuiltinFuture> TYPE = new WenyanType<>("builtin_future", WenyanBuiltinFuture.class);

    public IWenyanValue get() {
        return dummyRuntime.returnValue;
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
