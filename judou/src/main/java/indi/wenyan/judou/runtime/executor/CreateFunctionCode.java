package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.builtin_type.WenyanBuiltinFunction;

import java.util.ArrayList;

public enum CreateFunctionCode {
    ;

    static void createFunction(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        WenyanBuiltinFunction func = runtime.getProcessStack().pop().as(WenyanBuiltinFunction.TYPE);
        var newFunc = new WenyanBuiltinFunction(func.bytecode(), func.args(), new ArrayList<>());
        func.bytecode().getCapturedValues().stream()
                .map(v -> {
                    if (v.fromLocal()) {
                        if (v.index() == arg) return newFunc; // recursive call
                        return runtime.getLocals().get(v.index());
                    }
                    return runtime.getReferences().get(v.index());
                })
                .forEach(i -> {
                    assert newFunc.refs() != null;
                    newFunc.refs().add(i);
                });
        runtime.pushReturnValue(newFunc);
    }
}
