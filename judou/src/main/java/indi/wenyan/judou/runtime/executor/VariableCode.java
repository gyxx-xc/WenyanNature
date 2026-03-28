package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.ParsableType;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanLeftValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.language.JudouExceptionText;

/**
 * Handles variable operations in the Wenyan interpreter.
 */
public enum VariableCode {
    ;

    static void cast(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getProcessStack().pop();
        runtime.pushReturnValue(value.as(ParsableType.values()[arg].getType()));
    }

    static void setValue(IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getProcessStack().pop();
        IWenyanValue variable = runtime.getProcessStack().pop();
        if (variable instanceof WenyanLeftValue lv) {
            if (value == WenyanNull.NULL)
                lv.setValue(WenyanNull.NULL);
            else
                lv.setValue(value.as(lv.type()));
        } else
            throw new WenyanException(JudouExceptionText.SetValueToNonLeftValue.string());
    }

    static void store(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getProcessStack().pop();
        runtime.setLocal(arg, WenyanLeftValue.varOf(value));
    }

    static void loadGlobal(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        String id = runtime.getBytecode().getIdentifier(arg);
        IWenyanValue value = thread.getGlobalResolver().getGlobal(id);
        runtime.pushReturnValue(value);
    }

    static void loadRef(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getReferences().get(arg);
        runtime.pushReturnValue(value);
    }

    static void load(int arg, IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getLocals().get(arg);
        runtime.pushReturnValue(value);
    }
}
