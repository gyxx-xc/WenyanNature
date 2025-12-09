package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;

import java.util.Optional;

public interface ISingleTickExecCallHandler extends IExecCallHandler {
    @Override
    default Optional<IWenyanValue> handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        return Optional.of(handleOnce(context));
    }

    IWenyanValue handleOnce(JavacallContext context) throws WenyanException.WenyanThrowException;
}
