package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.values.primitive.WenyanList;

public enum CreateListCode {
    ;

    static void createList(IWenyanRunner thread) throws WenyanUnreachedException {
        thread.getCurrentRuntime().pushReturnValue(new WenyanList());
    }
}
