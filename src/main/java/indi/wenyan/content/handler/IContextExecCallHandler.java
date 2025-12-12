package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IHandleContext;

public interface IContextExecCallHandler extends IExecCallHandler {
    @Override
    default boolean handle(IHandleContext context, JavacallRequest request)
            throws WenyanException.WenyanThrowException {
        request.thread().currentRuntime().processStack.push(handleContext(context, request));
        return true;
    }

    IWenyanValue handleContext(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanTypeException;
}
