package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

/**
 * Wrapper for builtin functions in the Wenyan interpreter.
 * Manages function execution and result handling.
 */
public class WenyanInlineJavacall implements IJavacallHandler {
    private final WenyanValues.BuiltinFunction function;

    /**
     * Creates a new builtin function wrapper.
     *
     * @param function the function implementation
     */
    public WenyanInlineJavacall(WenyanValues.BuiltinFunction function) {
        this.function = function;
    }

    @Override
    public void call(IWenyanValue self, @UnknownNullability IWenyanRunner thread, List<IWenyanValue> argsList) throws WenyanException {
        thread.getCurrentRuntime().pushReturnValue(function.apply(self, argsList));
    }
}
