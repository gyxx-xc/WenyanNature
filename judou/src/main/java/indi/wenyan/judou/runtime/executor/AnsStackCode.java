package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanValue;

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
        // TODO: costy, consider ArrayCopy
        List<IWenyanValue> list = new ArrayList<>(arg);
        for (int i = 0; i < arg; i++) {
            list.add(runtime.getResultStack().pop());
            runtime.pushReturnValue(list.getLast());
        }
        for (var i : list) {
            runtime.getResultStack().push(i);
        }
    }

    static void peekAns(IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.pushReturnValue(runtime.getResultStack().peek());
    }

    static void popAns(IWenyanRunner thread) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.pushReturnValue(runtime.getResultStack().pop());
    }

    static void pushAns(IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        runtime.getResultStack().push(runtime.getProcessStack().pop());
    }
}
