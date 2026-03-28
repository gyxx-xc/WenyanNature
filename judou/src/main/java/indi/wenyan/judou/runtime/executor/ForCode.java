package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanInteger;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanValues;

import java.util.Iterator;

/**
 * Handles loop operations in the Wenyan interpreter.
 */
public enum ForCode {
    ;

    static void forNum(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getProcessStack().pop();
        int num = value.as(WenyanInteger.TYPE).value();
        if (num > 0) {
            runtime.pushReturnValue(WenyanValues.of((long) num - 1));
        } else {
            runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
            runtime.setPCFlag(true);
        }
    }

    static void forIter(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        Iterator<?> iter;
        assert runtime.getProcessStack().peek() != null;
        iter = runtime.getProcessStack().peek().as(WenyanList.WenyanIterator.TYPE).value();
        if (iter.hasNext()) {
            runtime.pushReturnValue((IWenyanValue) iter.next());
        } else {
            runtime.getProcessStack().pop();
            runtime.setProgramCounter(runtime.getBytecode().getLabel(arg));
            runtime.setPCFlag(true);
        }
    }
}
