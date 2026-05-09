package indi.wenyan.judou.api.exec;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.exec_interface.handler.IJavacallHandler;

import java.util.List;

public interface IRequestCallHandler
        extends IJavacallHandler {
    IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self,
                                  List<IWenyanValue> argsList) throws WenyanException;

    @Override
    default void call(IWenyanValue self, IWenyanRunner thread,
                      List<IWenyanValue> argsList) throws WenyanException {
        thread.platform().receive(newRequest(thread, self, argsList));
        thread.block();
    }
}
