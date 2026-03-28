package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.RunnerCreater;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.runtime.function_impl.WenyanProgramImpl;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.language.JudouTypeText;
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
        IThreadHolder<WenyanProgramImpl.PCB> newThread =
                RunnerCreater.newRunner(newRuntime, thread.getGlobalResolver());
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
