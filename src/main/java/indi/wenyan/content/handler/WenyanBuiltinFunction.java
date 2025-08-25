package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.List;

/**
 * Wrapper for builtin functions in the Wenyan interpreter.
 * Manages function execution and result handling.
 */
public class WenyanBuiltinFunction implements IJavacallHandler {
    private final BuiltinFunction function;

    /**
     * Creates a new builtin function wrapper.
     *
     * @param function the function implementation
     */
    public WenyanBuiltinFunction(BuiltinFunction function) {
        this.function = function;
    }

    /**
     * Handles the function call with the given parameters.
     *
     * @param self the self value
     * @param argsList the arguments for the call
     * @return the result of the function call
     * @throws WenyanException.WenyanThrowException if an error occurs during handling
     */
    public IWenyanValue handle(IWenyanValue self, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        return function.apply(self, argsList);
    }

    @Override
    public void call(IWenyanValue self, WenyanThread thread, List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
        thread.currentRuntime().processStack.push(handle(self, argsList));
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
         * @throws WenyanException.WenyanThrowException if an error occurs during function execution
         */
        IWenyanValue apply(IWenyanValue self, List<IWenyanValue> args)
                throws WenyanException.WenyanThrowException;
    }
}
