package indi.wenyan.interpreter.exec_interface.handler;

import indi.wenyan.interpreter.exec_interface.structure.IHandleableRequest;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.utils.WenyanThreading;

import java.util.List;

public interface RequestCallHandler
        extends IJavacallHandler {
    IHandleableRequest newRequest(WenyanThread thread, IWenyanValue self,
                                  List<IWenyanValue> argsList) throws WenyanThrowException;

    @Override
    @WenyanThreading
    default void call(IWenyanValue self, WenyanThread thread,
                     List<IWenyanValue> argsList) throws WenyanThrowException {
        thread.program.platform.receive(newRequest(thread, self, argsList));
        thread.block();
    }
}
