package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;

/// Runner interface exposed to executing Wenyan threads.
public interface IWenyanRunner extends IRunner {
    IGlobalResolver getGlobalResolver();

    IFrameManager<WenyanFrame> getFrameManager();

    default WenyanFrame getCurrentRuntime() throws WenyanUnreachedException {
        // since this one using too much, do a delegate to it.
        return getFrameManager().getCurrentRuntimeException();
    }
}
