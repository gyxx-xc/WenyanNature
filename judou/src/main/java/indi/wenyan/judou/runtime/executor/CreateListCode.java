package indi.wenyan.judou.runtime.executor;

import indi.wenyan.judou.runtime.function_impl.IWenyanRunner;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import org.jetbrains.annotations.UnknownNullability;

public class CreateListCode extends WenyanCode {

    protected CreateListCode() {
        super("CREATE_LIST");
    }

    @Override
    public void exec(int arg, @UnknownNullability IWenyanRunner thread) throws WenyanException {
        thread.getCurrentRuntime().pushReturnValue(new WenyanList());
    }
}
