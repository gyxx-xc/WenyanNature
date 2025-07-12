package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.IWenyanExecutor;

import java.util.List;
import java.util.Optional;

public interface IExecCallHandler extends IJavacallHandler {
    IWenyanValue handle(JavacallContext context) throws WenyanException.WenyanThrowException;

    default Optional<IWenyanExecutor> getExecutor() {
        return Optional.empty();
    }

    default void call(IWenyanValue self, WenyanThread thread,
                      List<IWenyanValue> argsList)
            throws WenyanException.WenyanThrowException {
        JavacallContext context = new JavacallContext(thread.program.warper, self, argsList,
                thread, this, thread.program.holder);

        if (getExecutor().isPresent())
            getExecutor().get().getExecQueue().receive(context);
        else
            // deprecated, use getExecutor()
            thread.program.requestThreads.add(context);
        thread.block();
    }
}
