package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.utils.language.Symbol;

import java.util.List;

public enum BreakpointCode {;
    public static void breakpoint(int ignoreArg, IWenyanRunner thread) throws WenyanException {
        thread.getGlobalResolver().getGlobal(Symbol.DEBUG_ID).as(IWenyanFunction.TYPE)
                .call(null, thread, List.of());
    }
}
