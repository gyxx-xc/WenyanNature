package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.judou.api.values.primitive.WenyanList;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

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
            IWenyanValue value1 = WenyanValues.of((long) num - 1);
            runtime.getProcessStack().push(value1);
        } else {
            runtime.setProgramCounter(arg);
            runtime.setPCFlag(true);
        }
    }

    static void forIter(int arg, IWenyanRunner thread) throws WenyanUnreachedException, WenyanException.WenyanTypeException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        Iterator<?> iter;
        assert runtime.getProcessStack().peek() != null;
        iter = runtime.getProcessStack().peek().as(WenyanList.WenyanIterator.TYPE).value();
        if (iter.hasNext()) {
            IWenyanValue value = (IWenyanValue) iter.next();
            runtime.getProcessStack().push(value);
        } else {
            runtime.getProcessStack().pop();
            runtime.setProgramCounter(arg);
            runtime.setPCFlag(true);
        }
    }
}
