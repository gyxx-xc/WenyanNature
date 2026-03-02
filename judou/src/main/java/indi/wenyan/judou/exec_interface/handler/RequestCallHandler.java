package indi.wenyan.judou.exec_interface.handler;

import indi.wenyan.judou.exec_interface.structure.IHandleableRequest;
import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanThreading;

import java.util.List;

public interface RequestCallHandler
        extends IJavacallHandler {
    IHandleableRequest newRequest(WenyanRunner thread, IWenyanValue self,
                                  List<IWenyanValue> argsList) throws WenyanException;

    @Override
    @WenyanThreading
    default void call(IWenyanValue self, WenyanRunner thread,
                     List<IWenyanValue> argsList) throws WenyanException {
        thread.platform().receive(newRequest(thread, self, argsList));
        thread.block();
    }
}
