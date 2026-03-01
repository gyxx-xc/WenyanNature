package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinFunction;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinFunctionTemplete;

public class CreateFunctionCode extends WenyanCode {
    protected CreateFunctionCode() {
        super("CREATE_FUNCTION");
    }

    @Override
    public void exec(int arg, WenyanThread thread) throws WenyanException {
        WenyanRuntime runtime = thread.currentRuntime();
        WenyanBuiltinFunctionTemplete func = runtime.getProcessStack().pop().as(WenyanBuiltinFunctionTemplete.TYPE);
        var refs = func.bytecode().getCapturedValues().stream().map(v -> v.fromLocal() ?
                runtime.getLocals().get(v.index()) : runtime.getReferences().get(v.index())).toList();
        runtime.pushReturnValue(new WenyanBuiltinFunction(func, refs));
    }
}
