package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles function calls in the Wenyan interpreter.
 */
public enum FunctionCode {
    ;

    // func / create_obj
    // -> ori_func / javacall

    // javacall
    //   A.B() a.B() B() -> exec
    //   A.b() b() -> exec
    //   a.b() -> set self, exec
    // native
    //   A.B() a.B() B() -> make self, call, capture return
    //   A.b() b() -> call
    //   a.b() -> set self, call
    static void callFunction(int arg, IWenyanRunner thread, boolean isAttr) throws WenyanException {
        WenyanFrame runtime = thread.getCurrentRuntime();
        IWenyanValue func = runtime.getProcessStack().pop();
        IWenyanValue self = null;
        IWenyanFunction callable;
        if (isAttr)
            self = runtime.getProcessStack().pop();

        // object_type
        if (func.is(IWenyanObjectType.TYPE)) {
            callable = func.as(IWenyanObjectType.TYPE);
        } else { // function
            // handleWarper self first
            if (isAttr) {
                // try casting to object (might be list)
                // if not, static method
                if (!self.is(IWenyanObject.TYPE)) {
                    self = null;
                }
            }
            callable = func.as(IWenyanFunction.TYPE);
        }

        List<IWenyanValue> argsList = new ArrayList<>(arg);
        for (int i = 0; i < arg; i++)
            argsList.add(runtime.getProcessStack().pop());

        // NOTE: must make the callF at end, because it may block thread
        //   which is a fake block, it will still run the rest command before blocked
        //   it will only block the next WenyanCode being executed
        callable.call(self, thread, argsList);
    }
}
