package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.builtin.WenyanBuiltinFunction;

public class CreateFunctionCode extends WenyanCode {
    protected CreateFunctionCode() {
        super("CREATE_FUNCTION");
    }

    @Override
    public void exec(int arg, WenyanRunner thread) throws WenyanException {
        WenyanRuntime runtime = thread.getCurrentRuntime();
        WenyanBuiltinFunction func = runtime.getProcessStack().pop().as(WenyanBuiltinFunction.TYPE);
        func.bytecode().getCapturedValues().stream()
                .map(v -> v.fromLocal() ?
                        runtime.getLocals().get(v.index()) : runtime.getReferences().get(v.index()))
                .forEach(i -> func.refs().add(i));
    }
}
