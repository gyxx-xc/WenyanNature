package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.exec_interface.handler.IJavacallHandler;
import indi.wenyan.judou.runtime.function_impl.WenyanRuntime;
import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanFunction;
import indi.wenyan.judou.structure.values.IWenyanObject;
import indi.wenyan.judou.structure.values.IWenyanObjectType;
import indi.wenyan.judou.structure.values.IWenyanValue;
import org.jetbrains.annotations.UnknownNullability;

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
        super(opName(o));
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
    public void exec(int args, @UnknownNullability WenyanThread thread) throws WenyanException {
        WenyanRuntime runtime = thread.currentRuntime();
        IWenyanValue func = runtime.getProcessStack().pop();
        IWenyanValue self = null;
        IWenyanFunction callable;
        if (operation == Operation.CALL_ATTR)
            self = runtime.getProcessStack().pop();

        // object_type
        if (func.is(IWenyanObjectType.TYPE)) {
            callable = func.as(IWenyanObjectType.TYPE);
        } else { // function
            // handleWarper self first
            if (operation == Operation.CALL_ATTR) {
                // try casting to object (might be list)
                // if not, ignore self
                self = self.tryAs(IWenyanObject.TYPE).orElse(null);
            }
            callable = func.as(IWenyanFunction.TYPE);
        }

        List<IWenyanValue> argsList = new ArrayList<>(args);
        for (int i = 0; i < args; i++)
            argsList.add(runtime.getProcessStack().pop());

        // NOTE: must make the callF at end, because it may block thread
        //   which is a fake block, it will still run the rest command before blocked
        //   it will only block the next WenyanCode being executed
        callable.call(self, thread, argsList);
    }

    @Override
    public int getStep(int args, @UnknownNullability WenyanThread thread) throws WenyanException {
        var function = thread.currentRuntime().getProcessStack().peek();
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
    private static String opName(Operation op) {
        return switch (op) {
            case CALL -> "CALL";
            case CALL_ATTR -> "CALL_ATTR";
        };
    }
}
