package indi.wenyan.content.handler;

import indi.wenyan.interpreter.structure.JavacallRequest;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IHandleContext;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface ISimpleExecCallHandler extends IExecCallHandler {
    @Override
    default boolean handle(IHandleContext context, JavacallRequest request) throws WenyanException.WenyanThrowException {
        request.thread().currentRuntime().processStack.push(handleOnce(request));
        return true;
    }

    IWenyanValue handleOnce(JavacallRequest request) throws WenyanException.WenyanThrowException;
}
