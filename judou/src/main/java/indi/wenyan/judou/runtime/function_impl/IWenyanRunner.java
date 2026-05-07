package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.runtime.IFrameManager;
import indi.wenyan.judou.runtime.IGlobalResolver;
import indi.wenyan.judou.runtime.IRunner;
import indi.wenyan.judou.structure.IHandleableException;
import indi.wenyan.judou.utils.UtilManager;
import org.slf4j.Logger;

public interface IWenyanRunner extends IRunner {
    IGlobalResolver getGlobalResolver();

    IFrameManager<WenyanFrame> getFrameManager();

    default WenyanFrame getCurrentRuntime() {
        // since this one using too much, do a delegate to it.
        return getFrameManager().getCurrentRuntimeException();
    }

    static boolean handleException(IWenyanRunner runner) {
        WenyanFrame runtime = runner.getFrameManager().getCurrentRuntime();
        while (runtime != null) {
            runtime = runner.getFrameManager().getCurrentRuntime();
            int programCounter = runtime.getProgramCounter();
            int handlerPc = runtime.getBytecode().getErrorHandler(programCounter);
            if (handlerPc != -1) {
                runtime.getProcessStack().clear();
                runtime.getResultStack().clear();
                runtime.setProgramCounter(handlerPc);
                return true;
            }
            runner.getFrameManager().ret();
        }
        return false;
    }

    static void dieWithException(IWenyanRunner runner, IHandleableException e) {
        Logger logger = UtilManager.getLogger();
        WenyanFrame frame = runner.getFrameManager().getCurrentRuntime();
        IHandleableException.ErrorContext errorContext;
        if (frame != null)
            errorContext = frame.getErrorContext();
        else
            errorContext = null;
        if (errorContext == null)
            logger.error("Unexpected, failed to get code context during handling an exception {}", e);
        e.handle(runner.platform()::handleError, logger, errorContext);
        runner.die();
    }
}
