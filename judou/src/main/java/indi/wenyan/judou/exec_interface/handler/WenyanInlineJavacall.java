package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Wrapper for builtin functions in the Wenyan interpreter.
 * Manages function execution and result handling.
 */
public record WenyanInlineJavacall(WenyanValues.BuiltinFunction function) implements IJavacallHandler {
    @Override
    public void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList,
                               Consumer<IWenyanValue> onReturn) throws WenyanException {
        onReturn.accept(function.apply(self, argsList));
    }
}
