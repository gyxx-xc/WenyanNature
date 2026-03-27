package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IFrameManager;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.LoggerManager;
import org.slf4j.Logger;

public interface IWenyanRunner extends IThreadHolder<WenyanProgramImpl.PCB> {
    IGlobalResolver getGlobalResolver();

    IFrameManager<WenyanFrame> getFrameManager();

    default WenyanFrame getCurrentRuntime() throws WenyanUnreachedException {
        // since this one using too much, do a delegate to it.
        return getFrameManager().getCurrentRuntime();
    }

    static void dieWithException(IWenyanRunner runner, WenyanException e) {
        Logger logger = LoggerManager.getLogger();
        WenyanFrame frame = runner.getFrameManager().getNullableCurrentRuntime();
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
