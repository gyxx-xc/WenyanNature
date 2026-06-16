package indi.wenyan.judou.api.exec;

import indi.wenyan.judou.api.exec.request.IHandleableRequest;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.exec_interface.handler.IJavacallHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface IRequestCallHandler
        extends IJavacallHandler, ICrossFunctionExecutable {
    IHandleableRequest newRequest(IWenyanRunner thread, IWenyanValue self,
                                  List<IWenyanValue> argsList, Consumer<IWenyanValue> onReturn) throws WenyanException;

    @Override
    default void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList, Consumer<IWenyanValue> onReturn) throws WenyanException {
        thread.platform().receive(newRequest(thread, self, argsList, onReturn));
        thread.block();
    }
}
