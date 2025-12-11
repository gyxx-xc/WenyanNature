package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.Optional;

public interface IReturnExecCallHandler extends IExecCallHandler {
    @Override
    default boolean handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return handleAndReturn(context).map(result -> {
            context.thread().currentRuntime().processStack.push(result);
            return true;
        }).orElse(false);
    }

    Optional<IWenyanValue> handleAndReturn(JavacallContext context) throws WenyanException.WenyanThrowException;
}
