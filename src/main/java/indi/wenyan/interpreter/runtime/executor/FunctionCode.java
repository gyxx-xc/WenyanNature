package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanDictObject;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanObjectType;
import indi.wenyan.interpreter.structure.WenyanValue;

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
            WenyanValue func = runtime.processStack.pop();
            WenyanValue.FunctionSign sign = (WenyanValue.FunctionSign)
                    func.casting(WenyanValue.Type.FUNCTION).getValue();
            WenyanValue self = null;
            boolean noReturn;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.getType() == WenyanValue.Type.OBJECT_TYPE) {
                // create empty, run constructor, return self
                self = new WenyanValue(WenyanValue.Type.OBJECT,
                        new WenyanDictObject((WenyanObjectType)
                                func.casting(WenyanValue.Type.OBJECT_TYPE).getValue()), true);
                runtime.processStack.push(self);
                noReturn = true;
            } else { // function
                // handle self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try {
                        self = self.casting(WenyanValue.Type.OBJECT);
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
        WenyanValue.FunctionSign sign;
        try {
            sign = (WenyanValue.FunctionSign)
                    thread.currentRuntime().processStack.peek()
                            .casting(WenyanValue.Type.FUNCTION).getValue();
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
