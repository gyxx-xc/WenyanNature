package indi.wenyan.judou.api.runtime;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.utils.UtilManager;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.function_impl.WenyanFrame;
import org.slf4j.Logger;

/// Runner interface expose to thread
public interface IWenyanRunner extends IRunner {
    IGlobalResolver getGlobalResolver();

    IFrameManager<WenyanFrame> getFrameManager();

    default WenyanFrame getCurrentRuntime() throws WenyanUnreachedException {
        // since this one using too much, do a delegate to it.
        return getFrameManager().getCurrentRuntimeException();
    }

    static void dieWithException(IWenyanRunner runner, WenyanException e) {
        Logger logger = UtilManager.getLogger();
        WenyanFrame frame = runner.getFrameManager().getCurrentRuntime();
        WenyanException.ErrorContext errorContext;
        if (frame != null)
            errorContext = frame.getErrorContext(e, logger);
        else
            errorContext = null;
        e.handle(runner.platform()::handleError, logger, errorContext);
        try {
            runner.die();
        } catch (WenyanUnreachedException e1) {
            logger.error("Unexpected, failed to die");
        }
    }
}
