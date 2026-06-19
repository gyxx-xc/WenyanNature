package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.api.values.primitive.WenyanList;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

public enum CreateListCode {
    ;

    static void createList(IWenyanRunner thread) throws WenyanUnreachedException {
        WenyanFrame wenyanFrame = thread.getCurrentRuntime();
        IWenyanValue value = new WenyanList();
        wenyanFrame.getProcessStack().push(value);
    }
}
