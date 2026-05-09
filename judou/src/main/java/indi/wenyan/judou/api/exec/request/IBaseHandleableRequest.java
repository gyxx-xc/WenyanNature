package indi.wenyan.judou.api.exec.request;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanUnreachedException;
import indi.wenyan.judou.api.exec.structure.IHandleContext;
import indi.wenyan.judou.api.exec.structure.IWenyanPlatform;
import indi.wenyan.judou.api.runtime.IWenyanRunner;

/// a simplicity impl for IHandleableRequest
public interface IBaseHandleableRequest extends IHandleableRequest {
    boolean handle(IHandleContext context) throws WenyanException;

    @Override
    default boolean run(IWenyanPlatform platform, IHandleContext context) {
        try {
            return handle(context);
        } catch (WenyanException e) {
            IWenyanRunner.dieWithException(thread(), e);
            return true;
        } catch (RuntimeException e) {
            IWenyanRunner.dieWithException(thread(), new WenyanUnreachedException.WenyanUnexceptedException(e));
            return true;
        }
    }
}
