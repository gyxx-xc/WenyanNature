package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleContext;
import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ISimpleExecCallHandler extends IExecCallHandler {
    @Override
    default boolean handle(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanThrowException {
        request.thread().currentRuntime().processStack.push(handle(request));
        return true;
    }

    IWenyanValue handle(JavacallRequest request) throws WenyanException.WenyanThrowException;
}
