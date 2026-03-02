package indi.wenyan.judou.structure.values.builtin;

import indi.wenyan.judou.compiler.WenyanBytecode;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanType;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

@Deprecated // not going to do until runtime refactored
public record WenyanBuiltinAsyncFunction(WenyanBytecode bytecode) implements IWenyanFunction {
    @Override
    public void call(IWenyanValue self, WenyanRunner thread,
                     List<IWenyanValue> argsList)
            throws WenyanException {
        var newThread = thread.forkRuntime(new WenyanRuntime(bytecode, List.of(), null));
        thread.program().create(newThread);
    }

    @Override
    public WenyanType<?> type() {
        return null;
    }
}
