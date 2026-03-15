package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IThreadHolder;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.utils.LoggerManager;
import org.slf4j.Logger;

public interface IWenyanRunner extends IThreadHolder<WenyanProgramImpl.PCB>, IFrameManager<WenyanFrame> {
    static void dieWithException(IWenyanRunner runner, WenyanException e) {
        Logger logger = LoggerManager.getLogger();
        e.handle(runner.platform()::handleError, logger,
                runner.getCurrentRuntime().getErrorContext(e, logger));
        try {
            runner.die();
        } catch (WenyanUnreachedException e1) {
            logger.error("Unexpected, failed to die");
        }
    }
}
