package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.language.Symbol;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanFunction;

import java.util.List;

public enum BreakpointCode {;
    public static void breakpoint(int ignoreArg, IWenyanRunner thread) throws WenyanException {
        thread.getGlobalResolver().getGlobal(Symbol.DEBUG_ID).as(IWenyanFunction.TYPE)
                .call(null, thread, List.of());
    }
}
