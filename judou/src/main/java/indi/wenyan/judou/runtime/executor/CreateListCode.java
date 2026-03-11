package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.WenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.warper.WenyanList;

public class CreateListCode extends WenyanCode {

    protected CreateListCode() {
        super("CREATE_LIST");
    }

    @Override
    public void exec(int arg, WenyanRunner thread) throws WenyanException {
        thread.getCurrentRuntime().pushReturnValue(new WenyanList());
    }
}
