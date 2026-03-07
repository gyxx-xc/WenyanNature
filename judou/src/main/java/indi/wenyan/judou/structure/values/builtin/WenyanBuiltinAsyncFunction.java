package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WenyanBuiltinAsyncFunction(WenyanBuiltinFunction func) implements IWenyanFunction {
    public static final WenyanType<WenyanBuiltinAsyncFunction> TYPE = new WenyanType<>("builtin_async_function", WenyanBuiltinAsyncFunction.class);

    @Override
    public void call(IWenyanValue self, WenyanRunner thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        var future = new WenyanBuiltinFuture();
        WenyanRuntime newRuntime = func.getNewRuntime(self, argsList, null);
        newRuntime.setReturnBehavior(future::onRunnerReturn);
        var newThread = thread.of(newRuntime);
        thread.program().create(newThread);
        thread.getCurrentRuntime().pushReturnValue(future);
    }

    @Override
    public @NotNull String toString() {
        return func.toString();
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }
}
