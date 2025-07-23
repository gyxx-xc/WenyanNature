package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanDevice;

import java.util.List;
import java.util.Optional;

public interface IExecCallHandler extends IJavacallHandler {
    IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException;

    Optional<IWenyanDevice> getExecutor();

    default void call(IWenyanValue self, WenyanThread thread,
                      List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        JavacallContext context = new JavacallContext(self, argsList,
                thread, this, thread.program.holder);

        getExecutor().ifPresentOrElse((executor) -> {
            thread.program.platform.accept(context);
        }, () -> {
            throw new WenyanException("killed by no executor");
        });
        thread.block();
    }
}
