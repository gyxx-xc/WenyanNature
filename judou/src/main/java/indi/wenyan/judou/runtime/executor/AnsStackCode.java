package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles operations related to the result stack in the Wenyan interpreter.
 */
public enum AnsStackCode {
    ;

    static void flush(IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.getResultStack().clear();
    }

    static void peekAnsN(int arg, IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        // TODO: costly, consider ArrayCopy
        List<IWenyanValue> list = new ArrayList<>(arg);
        for (int i = 0; i < arg; i++) {
            list.add(runtime.getResultStack().pop());
            runtime.getProcessStack().push(list.getLast());
        }
        for (var i : list) {
            runtime.getResultStack().push(i);
        }
    }

    static void peekAns(IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getResultStack().peek();
        runtime.getProcessStack().push(value);
    }

    static void popAns(IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue value = runtime.getResultStack().pop();
        runtime.getProcessStack().push(value);
    }

    static void pushAns(IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.getResultStack().push(runtime.getProcessStack().pop());
    }
}
