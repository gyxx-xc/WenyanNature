package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.content.handler.IJavacallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanFunction;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles function calls in the Wenyan interpreter.
 */
public class FunctionCode extends WenyanCode {
    private final Operation operation;

    /**
     * Creates a new FunctionCode with the specified operation.
     *
     * @param o The operation to perform
     */
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
            IWenyanValue func = runtime.processStack.pop();
            IWenyanValue self = null;
            IWenyanFunction callable;
            if (operation == Operation.CALL_ATTR)
                self = runtime.processStack.pop();

            // object_type
            if (func.type() == IWenyanObjectType.TYPE) {
                callable = func.as(IWenyanObjectType.TYPE);
            } else { // function
                // handleWarper self first
                if (operation == Operation.CALL_ATTR) {
                    // try casting to object (might be list)
                    try {
                        self = self.as(IWenyanObject.TYPE);
                    } catch (WenyanException.WenyanTypeException e) {
                        // ignore self then
                        self = null;
                    }
                }
                callable = func.as(IWenyanFunction.TYPE);
            }

            List<IWenyanValue> argsList = new ArrayList<>(args);
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
        var function = thread.currentRuntime().processStack.peek();
        if (!function.is(IWenyanFunction.TYPE))
            throw new WenyanException("無法調用非函數類型的值");
        return function.tryAs(IJavacallHandler.TYPE)
                .map(handler -> handler.getStep(args, thread))
                .orElse(args);
    }

    /**
     * Types of function call operations.
     */
    public enum Operation {
        CALL,
        CALL_ATTR
    }

    /**
     * Generates the name of the code based on the operation.
     *
     * @param op The operation
     * @return The name of the code
     */
    private static String name(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case CALL_ATTR -> "CALL_ATTR";
        };
    }
}
