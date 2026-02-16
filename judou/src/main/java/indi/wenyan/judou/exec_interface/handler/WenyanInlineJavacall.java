package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.runtime.function_impl.WenyanThread;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;

import java.util.List;

/**
 * Wrapper for builtin functions in the Wenyan interpreter.
 * Manages function execution and result handling.
 */
@SuppressWarnings("ClassCanBeRecord") // No it can't
public class WenyanInlineJavacall implements IJavacallHandler {
    private final BuiltinFunction function;

    /**
     * Creates a new builtin function wrapper.
     *
     * @param function the function implementation
     */
    public WenyanInlineJavacall(BuiltinFunction function) {
        this.function = function;
    }

    /**
     * Handles the function call with the given parameters.
     *
     * @param self the self value
     * @param argsList the arguments for the call
     * @return the result of the function call
     * @throws WenyanThrowException if an error occurs during handling
     */
    public IWenyanValue handle(IWenyanValue self, List<IWenyanValue> argsList) throws WenyanThrowException {
        return function.apply(self, argsList);
    }

    @Override
    public void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanThrowException {
        thread.currentRuntime().pushReturnValue(handle(self, argsList));
    }

    /**
     * Functional interface for builtin function implementations.
     */
    @FunctionalInterface
    public interface BuiltinFunction {
        /**
         * Applies the function to the given arguments.
         *
         * @param self the self value
         * @param args the function arguments
         * @return the result value
         * @throws WenyanThrowException if an error occurs during function execution
         */
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args)
                throws WenyanThrowException;
    }
}
