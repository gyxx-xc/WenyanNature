package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanFunction;
import indi.wenyan.interpreter.structure.values.WenyanObject;
import indi.wenyan.interpreter.structure.values.WenyanObjectType;
import indi.wenyan.interpreter.structure.values.WenyanValue;

import java.util.ArrayList;
import java.util.List;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        operation = o;
    }

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

    @Override
    public void exec(int args, WenyanThread thread) {
        try {
            WenyanRuntime runtime = thread.currentRuntime();
            WenyanValue func = runtime.processStack.pop();
            WenyanValue self = null;
            WenyanFunction callable;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.type() == WenyanObjectType.TYPE) {
                callable = func.as(WenyanObjectType.TYPE);
            } else { // function
                // handleWarper self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try {
                        self = self.as(WenyanObject.TYPE);
                    } catch (WenyanException.WenyanTypeException e) {
                        // ignore self then
                        self = null;
                    }
                }
                callable = func.as(WenyanFunction.TYPE);
            }

            List<WenyanValue> argsList = new ArrayList<>(args);
            for (int i = 0; i < args; i++)
                argsList.add(runtime.processStack.pop());

            // must make the callF at end, because it may block thread
            // which is a fake block, it will still run the rest command before blocked
            // it will only block the next WenyanCode being executed
            callable.call(self, thread, argsList);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) {
        WenyanFunction sign;
        try {
            sign = thread.currentRuntime().processStack.peek()
                    .as(WenyanFunction.TYPE);
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (sign instanceof JavacallHandler javacall) {
            return javacall.getStep(args, thread);
        } else {
            return args;
        }
    }

    public enum Operation {
        CALL,
        CALL_ATTR
    }

    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case CALL_ATTR -> "CALL_ATTR";
        };
    }
}
