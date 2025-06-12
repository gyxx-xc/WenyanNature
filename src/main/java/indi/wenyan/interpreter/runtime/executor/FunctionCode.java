package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;

public class FunctionCode extends WenyanCode {
    private final Operation operation;

    public FunctionCode(Operation o) {
        super(name(o));
        this.operation = o;
    }

    // func / create_obj
    // -> ori_func / javacall
    @Override
    public void exec(int args, WenyanThread thread) {
        try {
            WenyanRuntime runtime = thread.currentRuntime();
            WenyanNativeValue func = runtime.processStack.pop();
            WenyanNativeValue.FunctionSign sign = (WenyanNativeValue.FunctionSign)
                    func.casting(WenyanType.FUNCTION).getValue();
            WenyanNativeValue self = null;
            boolean noReturn;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.type() == WenyanType.OBJECT_TYPE) {
                // create empty, run constructor, return self
                self = new WenyanNativeValue(WenyanType.OBJECT,
                        new WenyanDictObject((WenyanObjectType)
                                func.casting(WenyanType.OBJECT_TYPE).getValue()), true);
                runtime.processStack.push(self);
                noReturn = true;
            } else { // function
                // handle self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try {
                        self = self.casting(WenyanType.OBJECT);
                    } catch (WenyanException.WenyanTypeException e) {
                        // ignore self then
                        self = null;
                    }
                }
                noReturn = false;
            }

            // must make the callF at end, because it may block thread
            // which is a fake block, it will still run the rest command before blocked
            // it will only block the next WenyanCode being executed
            sign.function().call(sign, self, thread, args, noReturn);
        } catch (WenyanException.WenyanThrowException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    @Override
    public int getStep(int args, WenyanThread thread) {
        WenyanNativeValue.FunctionSign sign;
        try {
            sign = (WenyanNativeValue.FunctionSign)
                    thread.currentRuntime().processStack.peek()
                            .casting(WenyanType.FUNCTION).getValue();
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
        if (sign.function() instanceof JavacallHandler javacall) {
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
