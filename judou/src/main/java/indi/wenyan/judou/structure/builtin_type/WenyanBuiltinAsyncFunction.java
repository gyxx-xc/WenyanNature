package indi.wenyan.judou.structure.builtin_type;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouTypeText;
import indi.wenyan.judou.api.runtime.IThreadHolder;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.runtime.RunnerCreator;
import indi.wenyan.judou.api.values.IWenyanFunction;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanSchedularImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WenyanBuiltinAsyncFunction(WenyanBuiltinFunction func) implements IWenyanFunction {
    public static final WenyanType<WenyanBuiltinAsyncFunction> TYPE = new WenyanType<>(JudouTypeText.BuiltinAsyncFunction.string(), WenyanBuiltinAsyncFunction.class);

    @Override
    public void call(IWenyanValue self, IWenyanRunner thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        var future = new WenyanBuiltinFuture();
        WenyanFrame newRuntime = func.getNewRuntime(self, argsList, null);
        newRuntime.setReturnBehavior(future::onRunnerReturn);
        IThreadHolder<WenyanSchedularImpl.PCB> newThread =
                RunnerCreator.newRunner(newRuntime, thread.getGlobalResolver());
        thread.create(newThread);
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
