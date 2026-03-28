package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.primitive.WenyanList;

public enum CreateListCode {
    ;

    static void createList(IWenyanRunner thread) throws WenyanUnreachedException {
        thread.getCurrentRuntime().pushReturnValue(new WenyanList());
    }
}
