package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.language.Symbol;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanFunction;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

public enum BreakpointCode {;
    public static void breakpoint(int ignoreArg, IWenyanRunner thread) throws WenyanException {
        IWenyanFunction iWenyanFunction = thread.getGlobalResolver().getGlobal(Symbol.DEBUG_ID).as(IWenyanFunction.TYPE);
        iWenyanFunction.callWithReturn((IWenyanValue) null, thread, List.of(), thread.getCurrentRuntime().getProcessStack()::push);
    }
}
